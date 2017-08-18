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
import cn.cerc.jdb.redis.RedisConnection;
import cn.cerc.jdb.redis.RedisSession;

public class StubHandle implements IHandle {
	private SqlSession mysqlSession;
	private MongoConnection mgConn;
	private QueueConnection queConn;
	private OssConnection ossConn;
	private JiguangConnection pushConn;
	private RedisConnection redis;
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
		
		//redis
		redis=new RedisConnection();
		redis.setConfig(config);
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
		if (RedisSession.sessionId.equals(key))
			return pushConn.getSession();
		return null;
	}

	// 关闭资源
	public void closeConnections() {
		mysqlSession.closeSession();
	}

	public void close() {
		closeConnections();
	}
}
