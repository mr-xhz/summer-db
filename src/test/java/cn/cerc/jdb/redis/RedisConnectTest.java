package cn.cerc.jdb.redis;

import org.junit.Test;

import cn.cerc.jdb.core.IConfig;

public class RedisConnectTest {
	private RedisConnection conn=new RedisConnection();
	@Test
	public void test() {
		conn.setConfig(new IConfig() {
			
			@Override
			public String getProperty(String key, String def) {
				if (RedisSession.redis_port.equals(key))
					return "127.0.0.1";
				if (RedisSession.redis_port.equals(key))
					return "6379";
				if (RedisSession.redis_password.equals(key))
					return "123456";
				
				return null;
			}
		});
	}

	@Test
	public void test1() {
		conn.init();
		RedisSession sess = conn.getSession();
		
		System.err.println(sess);
	}
}
