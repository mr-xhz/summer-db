package cn.cerc.jdb.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.mysql.SqlSession;

@Component
public class DaoHandle implements IHandle {
    @Autowired
    DataSource dataSource;
    private String corpNo = "master";
    private String userCode = "admin";
    private String userName = "admin";
    private Map<String, Object> items = new HashMap<>();
    private SqlSession session;

    @Override
    public String getCorpNo() {
        return corpNo;
    }

    @Override
    public String getUserCode() {
        return userCode;
    }

    @Override
    public Object getProperty(String key) {
        init();
        return items.get(key);
    }

    @Override
    public void closeConnections() {

    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setProperty(String key, Object value) {
        items.put(key, value);
    }

    @Override
    public boolean init(String bookNo, String userCode, String clientCode) {
        init();
        return true;
    }

    @Override
    public boolean init(String token) {
        init();
        return true;
    }

    @Override
    public boolean logon() {
        return true;
    }

    public void init() {
        if (session != null)
            return;
        session = new SqlSession();
        try {
            if (dataSource == null)
                throw new RuntimeException("dataSource is null");
            session.setConnection(dataSource.getConnection());
            items.put(SqlSession.sessionId, session);
            items.put(SqlSession.dataSource, dataSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCorpNo(String corpNo) {
        this.corpNo = corpNo;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
