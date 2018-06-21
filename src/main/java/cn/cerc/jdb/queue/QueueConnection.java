package cn.cerc.jdb.queue;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.IConnection;

public class QueueConnection implements IConnection {
    private static MNSClient client;
    private static CloudAccount account;
    private IConfig config;

    @Override
    public void setConfig(IConfig config) {
        this.config = config;
    }

    @Override
    public QueueSession getSession() {
        init();
        if (client == null)
            throw new RuntimeException("MNSClient is null");
        QueueSession sess = new QueueSession();
        sess.setClient(client);
        return sess;
    }

    public IConfig getConfig() {
        return config;
    }

    @Override
    public void init() {
        String server = config.getProperty(QueueSession.AccountEndpoint, null);
        String userCode = config.getProperty(QueueSession.AccessKeyId, null);
        String password = config.getProperty(QueueSession.AccessKeySecret, null);
        String token = config.getProperty(QueueSession.SecurityToken, "");
        if (server == null)
            throw new RuntimeException(QueueSession.AccountEndpoint + " 配置为空");
        if (userCode == null)
            throw new RuntimeException(QueueSession.AccessKeyId + " 配置为空");
        if (password == null)
            throw new RuntimeException(QueueSession.AccessKeySecret + " 配置为空");
        if (token == null)
            throw new RuntimeException(QueueSession.SecurityToken + " 配置为空");
        if (account == null) {
            account = new CloudAccount(userCode, password, server, token);
        }
        client = account.getMNSClient();
    }

}
