package cn.cerc.jdb.queue;

import org.apache.log4j.Logger;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.QueueMeta;

import cn.cerc.jdb.core.ISession;

public class QueueSession implements ISession {
    private static final Logger log = Logger.getLogger(QueueSession.class);

    public static final String AccessKeyId = "mns.accesskeyid";
    public static final String AccessKeySecret = "mns.accesskeysecret";
    public static final String AccountEndpoint = "mns.accountendpoint";
    public static final String SecurityToken = "mns.securitytoken";
    // IHandle中识别码
    public static String sessionId = "aliyunQueueSession";
    // 默认不可见时间
    private static int visibilityTimeout = 50;
    // 默认消息队列
    public static final String defaultQueue = QueueDB.SUMMER;
    // 连接客户端
    private MNSClient client;

    public MNSClient getClient() {
        return client;
    }

    public void setClient(MNSClient client) {
        this.client = client;
    }

    @Override
    public void closeSession() {
        // client 为线程安全，不需要多实例
        if (client != null) {
            client = null;
        }
    }

    /**
     * 根据队列的URL创建CloudQueue对象，后于后续对改对象的创建、查询等
     * 
     * @param queueCode
     *            队列代码
     * @return value 返回具体的消息队列
     */
    public CloudQueue openQueue(String queueCode) {
        return client.getQueueRef(queueCode);
    }

    /**
     * 创建队列
     * 
     * @param queueCode
     *            队列代码
     * @return value 返回创建的队列
     */
    public CloudQueue createQueue(String queueCode) {
        QueueMeta meta = new QueueMeta();
        // 设置队列的名字
        meta.setQueueName(queueCode);
        // 设置队列消息的长轮询等待时间，0为关闭长轮询
        meta.setPollingWaitSeconds(0);
        // 设置队列消息的最大长度，单位是byte
        meta.setMaxMessageSize(65356L);
        // 设置队列消息的最大长度，单位是byte
        meta.setMessageRetentionPeriod(72000L);
        // 设置队列消息的不可见时间，即取出消息隐藏时长，单位是秒
        meta.setVisibilityTimeout(180L);
        return client.createQueue(meta);
    }

    /**
     * 发送消息
     * 
     * @param queue
     *            消息队列
     * @param content
     *            消息内容
     * @return value 返回值，当前均为true
     */
    public boolean append(CloudQueue queue, String content) {
        Message message = new Message();
        message.setMessageBody(content);
        queue.putMessage(message);
        return true;
    }

    /**
     * 获取队列中的消息
     * 
     * @param queue
     *            消息队列
     * @return value 返回请求的删除，可为null
     */
    public Message receive(CloudQueue queue) {
        Message message = queue.popMessage();
        if (message != null) {
            log.debug("消息内容：" + message.getMessageBodyAsString());
            log.debug("消息编号：" + message.getMessageId());
            log.debug("访问代码：" + message.getReceiptHandle());
        } else {
            log.debug("msg is null");
        }
        return message;
    }

    /**
     * 删除消息
     * 
     * @param queue
     *            队列
     * @param receiptHandle
     *            消息句柄
     */
    public void delete(CloudQueue queue, String receiptHandle) {
        queue.deleteMessage(receiptHandle);
    }

    /**
     * 查看队列消息
     * 
     * @param queue
     *            队列
     * @return value 返回取得的消息体
     */
    public Message peek(CloudQueue queue) {
        return queue.peekMessage();
    }

    /**
     * 延长消息不可见时间
     * 
     * @param queue
     *            队列
     * @param receiptHandle
     *            消息句柄
     */
    public void changeVisibility(CloudQueue queue, String receiptHandle) {
        // 第一个参数为旧的ReceiptHandle值，第二个参数为新的不可见时间（VisibilityTimeout）
        String newReceiptHandle = queue.changeMessageVisibilityTimeout(receiptHandle, visibilityTimeout);
        log.debug("新的消息句柄: " + newReceiptHandle);
    }

}
