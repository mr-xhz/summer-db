package cn.cerc.jdb.mysql;

import static cn.cerc.jdb.core.Utils.safeString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.core.TDateTime;
import cn.cerc.jdb.core.Utils;

/**
 * 用于组合生成select指令，便于多条件查询编写
 * 
 * @author 张弓
 *
 */
public class BuildQuery {
    // private static final Logger log = Logger.getLogger(BuildSQL.class);
    private SqlQuery dataSet;
    private List<String> sqlWhere = new ArrayList<String>();
    private List<String> sqlText = new ArrayList<String>();
    public static final String vbCrLf = "\r\n";

    private String orderText;
    private String sql;
    private IHandle handle;

    public void setDataSet(SqlQuery dataSet) {
        this.dataSet = dataSet;
    }

    public BuildQuery(IHandle handle) {
        super();
        this.handle = handle;
    }

    /**
     * 增加自定义查询条件，须自行解决注入攻击！
     * 
     * @param param
     *            要加入的查询条件
     * @return 返回自身
     */
    public BuildQuery byParam(String param) {
        if (!"".equals(param))
            sqlWhere.add("(" + param + ")");
        return this;
    }

    public BuildQuery byLink(String[] fields, String value) {
        if (value == null || "".equals(value))
            return this;
        String str = "";
        String s1 = "%" + safeString(value).replaceAll("\\*", "") + "%";
        for (String sql : fields) {
            str = str + String.format("%s like '%s'", sql, s1);
            str = str + " or ";
        }
        str = str.substring(0, str.length() - 3);
        sqlWhere.add("(" + str + ")");
        return this;
    }

    public BuildQuery byNull(String field, boolean value) {
        String s = value ? "not null" : "null";
        sqlWhere.add(String.format("%s is %s", field, s));
        return this;
    }

    public BuildQuery byField(String field, String text) {
        String value = safeString(text);
        if ("".equals(value))
            return this;
        if ("*".equals(value))
            return this;
        if (value.contains("*")) {
            sqlWhere.add(String.format("%s like '%s'", field, value.replace("*", "%")));
            return this;
        }
        if ("``".equals(value)) {
            sqlWhere.add(String.format("%s='%s'", field, "`"));
            return this;
        }
        if ("`is null".equals(value)) {
            sqlWhere.add(String.format("(%s is null or %s='')", field, field));
            return this;
        }
        if (!value.startsWith("`")) {
            sqlWhere.add(String.format("%s='%s'", field, value));
            return this;
        }
        if ("`=".equals(value.substring(0, 2))) {
            sqlWhere.add(String.format("%s=%s", field, value.substring(2)));
            return this;
        }
        if ("`!=".equals(value.substring(0, 3)) || "`<>".equals(value.substring(0, 3))) {
            sqlWhere.add(String.format("%s<>%s", field, value.substring(3)));
            return this;
        }
        return this;
    }

    public BuildQuery byField(String field, int value) {
        sqlWhere.add(String.format("%s=%s", field, value));
        return this;
    }

    public BuildQuery byField(String field, double value) {
        sqlWhere.add(String.format("%s=%s", field, value));
        return this;
    }

    public BuildQuery byField(String field, TDateTime value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sqlWhere.add(String.format("%s='%s'", field, sdf.format(value.getData())));
        return this;
    }

    public BuildQuery byField(String field, boolean value) {
        int s = value ? 1 : 0;
        sqlWhere.add(String.format("%s=%s", field, s));
        return this;
    }

    public BuildQuery byBetween(String field, String value1, String value2) {
        sqlWhere.add(String.format("%s between '%s' and '%s'", field, safeString(value1), safeString(value2)));
        return this;
    }

    public BuildQuery byBetween(String field, int value1, int value2) {
        sqlWhere.add(String.format("%s between %s and %s", field, value1, value2));
        return this;
    }

    public BuildQuery byBetween(String field, double value1, double value2) {
        sqlWhere.add(String.format("%s between %s and %s", field, value1, value2));
        return this;
    }

    public BuildQuery byBetween(String field, TDateTime value1, TDateTime value2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sqlWhere.add(String.format(" %s between '%s' and '%s' ", field, sdf.format(value1.getData()),
                sdf.format(value2.getData())));
        return this;
    }

    public BuildQuery byRange(String field, String... values) {
        // where code_ in ("aa","Bb")
        if (values.length > 0) {
            String s = field + " in (";
            for (String val : values) {
                s = s + "'" + safeString(val) + "',";
            }
            s = s.substring(0, s.length() - 1) + ")";
            sqlWhere.add(s);
        }
        return this;
    }

    public BuildQuery byRange(String field, int[] values) {
        if (values.length > 0) {
            String s = field + " in (";
            for (int sql : values) {
                s = s + sql + ",";
            }
            s = s.substring(0, s.length() - 1) + ")";
            sqlWhere.add(s);
        }
        return this;
    }

    public BuildQuery byRange(String field, double[] values) {
        if (values.length > 0) {
            String s = field + " in (";
            for (double sql : values) {
                s = s + sql + ",";
            }
            s = s.substring(0, s.length() - 1) + ")";
            sqlWhere.add(s);
        }
        return this;
    }

    public BuildQuery add(String text) {
        String regex = "((\\bselect)|(\\bSelect)|(\\s*select)|(\\s*Select))\\s*(distinct)*\\s+%s";
        if (text.matches(regex))
            text = text.replaceFirst("%s", "");
        sqlText.add(text);
        return this;
    }

    public BuildQuery add(String fmtText, Object... args) {
        ArrayList<Object> items = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof String) {
                items.add(Utils.safeString((String) arg));
            } else {
                items.add(arg);
            }
        }
        sqlText.add(String.format(fmtText, items.toArray()));
        return this;
    }

    public SqlQuery getDataSet() {
        if (this.dataSet == null)
            this.dataSet = new SqlQuery(handle);
        return this.dataSet;
    }

    protected String getSelectCommand() {
        if (this.sql != null) {
            sql = sql.replaceFirst("%s", "");
            return this.sql;
        }
        StringBuffer str = new StringBuffer();
        for (String sql : sqlText) {
            if (str.length() > 0)
                str.append(vbCrLf);
            str.append(sql);
        }
        if (sqlWhere.size() > 0) {
            if (str.length() > 0)
                str.append(vbCrLf);
            str.append("where ");
            for (String sql : sqlWhere) {
                str.append(sql).append(" and ");
            }
            str.setLength(str.length() - 5);
        }
        if (orderText != null)
            str.append(vbCrLf).append(orderText);

        String sqls = str.toString().trim();
        sqls = sqls.replaceAll(" %s ", " ");
        return sqls;
    }

    public String getCommandText() {
        String sql = getSelectCommand();
        if ("".equals(sql))
            return sql;
        if (getDataSet().getMaximum() > -1)
            return sql + " limit " + getDataSet().getMaximum();
        else
            return sql;
    }

    public SqlQuery open() {
        SqlQuery ds = getDataSet();
        ds.emptyCommand().add(this.getSelectCommand());
        ds.open();
        return ds;
    }

    public SqlQuery openReadonly() {
        SqlQuery ds = getDataSet();
        ds.emptyCommand().add(this.getSelectCommand());
        ds.openReadonly();
        return ds;
    }

    public SqlQuery open(Record head, Record foot) {
        SqlQuery ds = getDataSet();
        if (head.exists("__offset__"))
            this.setOffset(head.getInt("__offset__"));
        ds.emptyCommand().add(this.getSelectCommand());
        ds.open();
        if (foot != null)
            foot.setField("__finish__", ds.getFetchFinish());
        return ds;
    }

    // @Override
    public void close() {
        sql = null;
        sqlText.clear();
        sqlWhere.clear();
        orderText = null;
        if (this.dataSet != null)
            this.dataSet.close();
    }

    public int getOffset() {
        return getDataSet().getOffset();
    }

    public BuildQuery setOffset(int offset) {
        getDataSet().setOffset(offset);
        return this;
    }

    public int getMaximum() {
        return getDataSet().getMaximum();
    }

    public BuildQuery setMaximum(int maximum) {
        getDataSet().setMaximum(maximum);
        return this;
    }

    public void setOrderText(String orderText) {
        this.orderText = orderText;
    }

    public String getOrderText() {
        return this.orderText;
    }
}
