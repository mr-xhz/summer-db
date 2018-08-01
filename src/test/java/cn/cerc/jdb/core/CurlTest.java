package cn.cerc.jdb.core;

import org.apache.log4j.Logger;
import org.junit.Test;

public class CurlTest {

    private static final Logger log = Logger.getLogger(Curl.class);

    @Test
    public void test() {
        String host = "http://smapi.sanmaoyou.com/api/tips/list/v2";
//        String host = "https://www.jayun.site/api/message";

        Curl curl = new Curl();
        log.info(curl.doPost(host));
    }

}
