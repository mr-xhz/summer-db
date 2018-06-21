package cn.cerc.jdb.sms;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cn.cerc.jdb.core.IConfig;

public class AliyundysmsTest {

    @Test
    public void testSend() {
        Aliyundysms sms = new Aliyundysms(new IConfig() {

            @Override
            public String getProperty(String key, String def) {
                if (Aliyundysms.aliyun_accessKeyId.equals(key)) {
                    return "your aliyun accessKeyId";
                }
                if (Aliyundysms.aliyun_accessSecret.equals(key)) {
                    return "your aliyun accessSecret";
                }
                if (Aliyundysms.SingName.equals(key)) {
                    return "地藤";
                }
                return null;
            }

            @Override
            public String getProperty(String key) {
                return this.getProperty(key, null);
            }
        });

        sms.setPhoneNumbers("18566767108");
        sms.setTemplateCode("SMS_63405184");
        String smsParam = "{\"code\":\"658740\",\"product\":\"地藤\"}";
        boolean ok = sms.send("911001", smsParam);
        System.out.println(sms.getMessage());
        assertTrue(ok);
    }

}
