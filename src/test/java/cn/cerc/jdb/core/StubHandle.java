package cn.cerc.jdb.core;

import cn.cerc.jdb.jiguang.JiguangConnection;
import cn.cerc.jdb.jiguang.JiguangSession;
import cn.cerc.jdb.mongo.MongoConnection;
import cn.cerc.jdb.mongo.MongoSession;
import cn.cerc.jdb.mysql.SqlConnection;
import cn.cerc.jdb.mysql.SqlSession;
import cn.cerc.jdb.oss.OssConnection;
import cn.cerc.jdb.oss.OssSession;
import cn.cerc.jdb.queue.QueueConnection;
import cn.cerc.jdb.queue.QueueSession;

public class StubHandle implements IHandle {
    private SqlSession mysqlSession;
    private MongoConnection mgConn;
    private QueueConnection queConn;
    private OssConnection ossConn;
    private JiguangConnection pushConn;

    public StubHandle() {
        super();
        IConfig config = new StubConfig();

        // mysql
        SqlConnection conn = new SqlConnection();
        conn.setConfig(config);
        mysqlSession = conn.getSession();

        // mongodb
        mgConn = new MongoConnection();
        mgConn.setConfig(config);

        // aliyun mq
        queConn = new QueueConnection();
        queConn.setConfig(config);

        // oss
        ossConn = new OssConnection();
        ossConn.setConfig(config);

        // Jiguang
        pushConn = new JiguangConnection();
        pushConn.setConfig(config);
    }

    @Override
    public String getCorpNo() {
        throw new RuntimeException("corpNo is null");
    }

    @Override
    public String getUserCode() {
        throw new RuntimeException("userCode is null");
    }

    @Override
    public Object getProperty(String key) {
        if (SqlSession.sessionId.equals(key))
            return mysqlSession;
        if (MongoSession.sessionId.equals(key))
            return mgConn.getSession();
        if (QueueSession.sessionId.equals(key))
            return queConn.getSession();
        if (OssSession.sessionId.equals(key))
            return ossConn.getSession();
        if (JiguangSession.sessionId.equals(key))
            return pushConn.getSession();
        return null;
    }

    // 关闭资源
    @Override
    public void closeConnections() {
        mysqlSession.closeSession();
    }

    public void close() {
        closeConnections();
    }

    // 用户姓名
    @Override
    public String getUserName() {
        return getUserCode();
    }

    // 设置自定义参数
    @Override
    public void setProperty(String key, Object value) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    // 直接设置成登录成功状态，用于定时服务时初始化等，会生成内存临时的token
    @Override
    public boolean init(String bookNo, String userCode, String clientCode) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    // 在登录成功并生成token后，传递token值进行初始化
    @Override
    public boolean init(String token) {
        throw new RuntimeException("调用了未被实现的接口");
    }

    // 返回当前是否为已登入状态
    @Override
    public boolean logon() {
        return false;
    }

}
