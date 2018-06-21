package cn.cerc.jdb.queue;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;

import cn.cerc.jdb.core.Utils;

public class Queue {
    private static final Logger log = Logger.getLogger(Queue.class);
    private CloudQueue client;
    private String receiptHandle;
    private Message message;

    public Queue(CloudQueue client) {
        this.client = client;
    }

    public String read() {
        message = client.popMessage();
        if (message != null) {
            log.debug("消息内容：" + message.getMessageBodyAsString());
            log.debug("消息编号：" + message.getMessageId());
            log.debug("访问代码：" + message.getReceiptHandle());
            log.debug(message.getMessageBody());
            receiptHandle = message.getReceiptHandle();
            return message.getMessageBody();
        } else {
            return null;
        }
    }

    public Object readObject() {
        try {
            String str = this.read();
            if (str == null)
                return null;
            return Utils.deserializeToObject(str);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void append(String content) {
        message = new Message();
        message.setMessageBody(content);
        client.putMessage(message);
    }

    public void appendObject(Object obj) {
        try {
            this.append(Utils.serializeToString(obj));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        if (receiptHandle == null)
            return;
        client.deleteMessage(receiptHandle);
        receiptHandle = null;
        return;
    }

    public String getBodyText() {
        return message != null ? message.getMessageBody() : null;
    }

    public Message getMessage() {
        return message;
    }

}
