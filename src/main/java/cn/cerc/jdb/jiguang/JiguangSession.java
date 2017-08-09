package cn.cerc.jdb.jiguang;

import cn.cerc.jdb.core.ISession;
import cn.jpush.api.JPushClient;

public class JiguangSession implements ISession {
    // IHandle中识别码
    public static final String sessionId = "jiguangSession";
    // 配置文件
    public static final String masterSecret = "jiguang.masterSecret";
    public static final String appKey = "jiguang.appKey";
    // 连接
    private JPushClient client;

    @Override
    public void closeSession() {
        // client = null;
    }

    public JPushClient getClient() {
        return client;
    }

    public void setClient(JPushClient client) {
        this.client = client;
    }

}
