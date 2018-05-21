package cn.cerc.jdb.queue;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;

import cn.cerc.jdb.core.ServerConfig;

public class QueueFactory {
    private static MNSClient client;
    private static CloudAccount account;

    static {
        ServerConfig config = ServerConfig.getInstance();
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

    public static Queue getQueue(String queueCode) {
        CloudQueue queue = client.getQueueRef(queueCode);
        return new Queue(queue);
    }
}
