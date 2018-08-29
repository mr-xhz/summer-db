package cn.cerc.jdb.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.DataSetEvent;
import cn.cerc.jdb.core.DataSetState;
import cn.cerc.jdb.core.FieldDefs;
import cn.cerc.jdb.core.IDataOperator;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;

public class SqlQuery extends DataQuery {
    private static final Logger log = Logger.getLogger(SqlQuery.class);

    private static final long serialVersionUID = 7316772894058168187L;
    private SqlSession session;
    private SqlSession slaveSession;

    private DataSource dataSource;
    private DataSource slaveDataSource;
    // private boolean closeMax = false;
    private int offset = 0;
    private int maximum = BigdataException.MAX_RECORDS;
    // 若数据有取完，则为true，否则为false
    private boolean fetchFinish;
    // 数据库保存操作执行对象
    private IDataOperator operator;
    // 仅当batchSave为true时，delList才有记录存在
    private List<Record> delList = new ArrayList<>();

    @Override
    public void close() {
        this.active = false;
        this.operator = null;
        super.close();
    }

    public SqlQuery(IHandle handle) {
        super(handle);
        this.session = (SqlSession) handle.getProperty(SqlSession.sessionId);
        this.slaveSession = (SqlSession) handle.getProperty(SqlSession.slaveSessionId);

        this.dataSource = (DataSource) handle.getProperty(SqlSession.dataSource);
        this.slaveDataSource = (DataSource) handle.getProperty(SqlSession.slaveDataSource);
    }

    private Statement getStatement(boolean isSlave) throws SQLException {
        try {
            if (isSlave) {
                if (this.slaveDataSource == null) {
                    if (this.dataSource == null) {
                        if (this.slaveSession == null) {
                            return this.session.getConnection().createStatement();
                        } else {
                            return this.slaveSession.getConnection().createStatement();
                        }
                    } else {
                        return this.dataSource.getConnection().createStatement();
                    }
                } else {
                    return this.slaveDataSource.getConnection().createStatement();
                }
            } else {
                if (this.dataSource == null) {
                    return this.session.getConnection().createStatement();
                } else {
                    return this.dataSource.getConnection().createStatement();
                }
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    private void closeStatement(Statement statement) {
        try {
            Connection conn = statement.getConnection();
            // statement.close();
            if (this.slaveDataSource == null) {
                if (this.dataSource == null) {
                } else {
                    conn.close();
                }
            } else {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DataQuery open() {
        if (session == null)
            throw new RuntimeException("SqlConnection is null");
        return this._open(false);
    }

    public DataQuery openReadonly() {
        return this._open(true);
    }

    private DataQuery _open(boolean isSlave) {

        String sql = getSelectCommand();
        Statement st = null;
        try {
            this.fetchFinish = true;
            st = this.getStatement(isSlave);
            log.debug(sql.replaceAll("\r\n", " "));
            st.execute(sql.replace("\\", "\\\\"));
            try (ResultSet rs = st.getResultSet()) {
                // 取出所有数据
                append(rs);
                this.first();
                this.active = true;
                return this;
            }
        } catch (SQLException e) {
            log.error(sql);
            throw new RuntimeException(e.getMessage());
        } finally {
            this.closeStatement(st);
        }
    }

    // 追加相同数据表的其它记录，与已有记录合并
    public int attach(String sql) {
        if (!this.active) {
            this.clear();
            this.add(sql);
            this.open();
            return this.size();
        }
        if (session == null)
            throw new RuntimeException("SqlSession is null");
        Connection conn = session.getConnection();
        if (conn == null)
            throw new RuntimeException("Connection is null");
        try {
            try (Statement st = conn.createStatement()) {
                log.debug(sql.replaceAll("\r\n", " "));
                st.execute(sql.replace("\\", "\\\\"));
                try (ResultSet rs = st.getResultSet()) {
                    int oldSize = this.size();
                    append(rs);
                    return this.size() - oldSize;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void append(ResultSet rs) throws SQLException {
        DataSetEvent onAfterAppend = this.getOnAfterAppend();
        try {
            this.setOnAfterAppend(null);
            rs.last();
            if (this.maximum > -1)
                BigdataException.check(this, this.size() + rs.getRow());
            // 取得字段清单
            ResultSetMetaData meta = rs.getMetaData();
            FieldDefs defs = this.getFieldDefs();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String field = meta.getColumnLabel(i);
                if (!defs.exists(field))
                    defs.add(field);
            }
            // 取得所有数据
            if (rs.first()) {
                int total = this.size();
                do {
                    total++;
                    if (this.maximum > -1 && this.maximum < total) {
                        this.fetchFinish = false;
                        break;
                    }
                    Record record = append().getCurrent();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        String fn = rs.getMetaData().getColumnLabel(i);
                        record.setField(fn, rs.getObject(fn));
                    }
                    record.setState(DataSetState.dsNone);

                } while (rs.next());
            }
        } finally {
            this.setOnAfterAppend(onAfterAppend);
        }
    }

    @Override
    public SqlQuery setActive(boolean value) {
        if (value) {
            if (!this.active)
                this.open();
            this.active = true;
        } else {
            this.close();
        }
        return this;
    }

    @Override
    public boolean getActive() {
        return active;
    }

    @Override
    public void post() {
        if (this.isBatchSave())
            return;
        Record record = this.getCurrent();
        if (record.getState() == DataSetState.dsInsert) {
            beforePost();
            getDefaultOperator().insert(record);
            super.post();
        } else if (record.getState() == DataSetState.dsEdit) {
            beforePost();
            getDefaultOperator().update(record);
            super.post();
        }
    }

    @Override
    public void delete() {
        Record record = this.getCurrent();
        super.delete();
        if (record.getState() == DataSetState.dsInsert)
            return;
        if (this.isBatchSave())
            delList.add(record);
        else {
            getDefaultOperator().delete(record);
        }
    }

    @Override
    public void save() {
        if (!this.isBatchSave())
            throw new RuntimeException("batchSave is false");
        IDataOperator operator = getDefaultOperator();
        // 先执行删除
        for (Record record : delList)
            operator.delete(record);
        delList.clear();
        // 再执行增加、修改
        this.first();
        while (this.fetch()) {
            if (this.getState().equals(DataSetState.dsInsert)) {
                beforePost();
                operator.insert(this.getCurrent());
                super.post();
            } else if (this.getState().equals(DataSetState.dsEdit)) {
                beforePost();
                operator.update(this.getCurrent());
                super.post();
            }
        }
    }

    protected IDataOperator getDefaultOperator() {
        if (operator == null) {
            SqlOperator def = new SqlOperator(this.handle);
            String sql = this.getCommandText();
            if (sql != null)
                def.setTableName(SqlOperator.findTableName(sql));
            operator = def;
        }
        if (operator instanceof SqlOperator) {
            SqlOperator opear = (SqlOperator) operator;
            if (opear.getTableName() == null) {
                String sql = this.getCommandText();
                if (sql != null)
                    opear.setTableName(SqlOperator.findTableName(sql));
            }
        }
        return operator;
    }

    @Override
    public IDataOperator getOperator() {
        return operator;
    }

    public void setOperator(IDataOperator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        StringBuffer sl = new StringBuffer();
        sl.append(String.format("[%s]%n", this.getClass().getName()));
        sl.append(String.format("CommandText:%s%n", this.getCommandText()));
        sl.append(String.format("RecordCount:%d%n", this.size()));
        sl.append(String.format("RecNo:%d%n", this.getRecNo()));
        return sl.toString();
    }

    public int getOffset() {
        return offset;
    }

    public SqlQuery setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getMaximum() {
        return maximum;
    }

    public SqlQuery setMaximum(int maximum) {
        if (maximum > BigdataException.MAX_RECORDS) {
            String str = String.format("本次请求的记录数超出了系统最大笔数为  %d 的限制！", BigdataException.MAX_RECORDS);
            throw new RuntimeException(str);
        }
        this.maximum = maximum;
        return this;
    }

    protected String getSelectCommand() {

        String sql = this.getCommandText();
        if (sql == null || sql.equals(""))
            throw new RuntimeException("[TAppQuery]CommandText is null ！");

        if (sql.indexOf("call ") > -1)
            return sql;

        if (this.offset > 0) {
            if (this.maximum < 0)
                sql = sql + String.format(" limit %d,%d", this.offset, BigdataException.MAX_RECORDS + 1);
            else
                sql = sql + String.format(" limit %d,%d", this.offset, this.maximum + 1);
        } else if (this.maximum == BigdataException.MAX_RECORDS) {
            sql = sql + String.format(" limit %d", this.maximum + 2);
        } else if (this.maximum > -1) {
            sql = sql + String.format(" limit %d", this.maximum + 1);
        } else if (this.maximum == 0) {
            sql = sql + String.format(" limit %d", 0);
        }
        return sql;
    }

    public boolean getFetchFinish() {
        return fetchFinish;
    }

    public void clear() {
        close();
        this.emptyCommand();
    }
}
