package cn.cerc.jdb.jiguang;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.IConnection;
import cn.jpush.api.JPushClient;

public class JiguangConnection implements IConnection {
    private static JPushClient client = null;
    private static JiguangSession session;
    private IConfig config;

    @Override
    public void setConfig(IConfig config) {
        this.config = config;
    }

    public IConfig getConfig() {
        return config;
    }

    @Override
    public void init() {
        if (session == null) {
            String masterSecret = config.getProperty(JiguangSession.masterSecret);
            if (masterSecret == null)
                throw new RuntimeException("jiguang.masterSecret is null");

            String appKey = config.getProperty(JiguangSession.appKey);
            if (appKey == null)
                throw new RuntimeException("jiguang.appKey is null");

            client = new JPushClient(masterSecret, appKey);
            session = new JiguangSession();
            session.setClient(client);
        }
    }

    @Override
    public JiguangSession getSession() {
        this.init();
        return session;
    }
}
