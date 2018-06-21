package cn.cerc.jdb.queue;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.ServerConfig;

public class QueueHandle implements IHandle {
    private String corpNo;
    private String userCode;
    private String userName;
    private Map<String, Object> items = new HashMap<>();

    public QueueHandle() {
        QueueConnection connection = new QueueConnection();
        connection.setConfig(ServerConfig.getInstance());
        items.put(QueueSession.sessionId, connection.getSession());
    }

    @Override
    public String getCorpNo() {
        return this.corpNo;
    }

    @Override
    public String getUserCode() {
        return this.userCode;
    }

    @Override
    public Object getProperty(String key) {
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
        return true;
    }

    @Override
    public boolean init(String token) {
        return true;
    }

    @Override
    public boolean logon() {
        return true;
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
