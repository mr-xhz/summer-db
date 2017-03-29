package cn.cerc.jdb.aliyu;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.core.IConfig;

public class AlidayuTest {

	@Test
	@Ignore
	public void test() {
		Alidayu sms = new Alidayu(new IConfig() {

			@Override
			public String getProperty(String key, String def) {
				if (Alidayu.AppKey.equals(key))
					return "appkey";
				if (Alidayu.AppSecret.equals(key))
					return "appsecret";
				if (Alidayu.SingName.equals(key))
					return "我的应用";
				return null;
			}

		});
		sms.setMobileNo("13912345678");
		sms.setTemplateNo("SMS_1190006");
		boolean ok = sms.send("000000", "");
		System.out.println(sms.getMessage());
		assertTrue(ok);
	}

}
