package cn.cerc.jdb.mongo;

import org.junit.Before;
import org.junit.Test;

import com.mongodb.client.MongoDatabase;

import cn.cerc.jdb.core.IConfig;

public class MongoConnectionTest {
	private MongoConnection conn = new MongoConnection();

	@Before
	public void setUp() throws Exception {
		conn.setConfig(new IConfig() {
			@Override
			public String getProperty(String key, String def) {
				if (MongoSession.mgdb_site.equals(key))
					return "127.0.0.1:3717";
				if (MongoSession.mgdb_password.equals(key))
					return "password";
				if (MongoSession.mgdb_username.equals(key))
					return "root";
				if (MongoSession.mgdb_dbname.equals(key))
					return "admin";
				return null;
			}
		});
	}

	@Test
	public void test() {
		conn.init();
		MongoSession sess = conn.getSession();
		MongoDatabase db = sess.getDatabase();
		db.createCollection("temp");
		System.err.println(db.getName());
	}

}
