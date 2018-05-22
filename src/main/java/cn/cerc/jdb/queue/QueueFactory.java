package cn.cerc.jdb.queue;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;

import cn.cerc.jdb.core.ServerConfig;

public class QueueFactory {
    private static MNSClient client;
    private static CloudAccount account;
    private static ServerConfig config;

    static {
        config = ServerConfig.getInstance();
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

    public static Queue getQueue(String propertKey) {
        String queueId = config.getProperty(propertKey);
        if (queueId == null)
            throw new RuntimeException("application.properties 中没有找到配置项：" + propertKey);
        
        CloudQueue queue = client.getQueueRef(queueId);
        return new Queue(queue);
    }
}
