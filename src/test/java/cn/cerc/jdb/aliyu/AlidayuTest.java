package cn.cerc.jdb.aliyu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cn.cerc.jdb.core.IConfig;

public class AlidayuTest {

	@Test
	public void testSend() {
		Alidayu sms = new Alidayu(new IConfig() {

			@Override
			public String getProperty(String key, String def) {

				if (Alidayu.AppName.equals(key))
					return "地藤";
				if (Alidayu.ServerUrl.equals(key))
					return "http://gw.api.taobao.com/router/rest";
				if (Alidayu.AppKey.equals(key))
					return "23256148";
				if (Alidayu.AppSecret.equals(key))
					return "8f8a19b62ac55b11ed3b7a0c241f218d";
				if (Alidayu.SingName.equals(key))
					return "地藤";
				return null;
			}
		});

		sms.setMobileNo("18566767108");
		sms.setTemplateNo("SMS_1190006");

		String json = "{\"code\":\"123\",\"product\":\"567\"}";
		boolean ok = sms.send("911001", json);
		System.out.println(sms.getMessage());
		assertTrue(ok);
	}

}
