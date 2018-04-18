package cn.cerc.jdb.queue;

import org.apache.log4j.Logger;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.google.gson.JsonSyntaxException;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IHandle;

public class QueueQuery extends DataQuery {
    private static final Logger log = Logger.getLogger(QueueQuery.class);
    private static final long serialVersionUID = 7781788221337787366L;
    private QueueOperator operator;
    private String queueCode;
    private QueueSession sess;
    private CloudQueue queue;
    private String receiptHandle;
    private QueueMode queueMode = QueueMode.append;

    public QueueQuery(IHandle handle) {
        super(handle);
        this.setBatchSave(true);
        this.sess = (QueueSession) handle.getProperty(QueueSession.sessionId);
    }

    @Override
    public DataQuery open() {
        if (queueCode == null) {
            queueCode = getOperator().findTableName(this.getCommandText());
            queue = sess.openQueue(queueCode);
        }
        if (null == queueCode || "".equals(queueCode))
            throw new RuntimeException("queueCode is null");
        if (this.active)
            throw new RuntimeException("active is true");

        // 当maximum设置为1时，读取消息
        if (this.queueMode == QueueMode.recevie) {
            Message message = sess.receive(queue);
            if (message != null) {
                try {
                    this.setJSON(message.getMessageBody());
                    receiptHandle = message.getReceiptHandle();
                    this.setActive(true);
                } catch (JsonSyntaxException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return this;
    }

    @Override
    public void save() {
        if (this.queueMode != QueueMode.append)
            throw new RuntimeException("当前作业模式下，不允许保存");
        sess.append(queue, getJSON());
        log.info("消息保存成功");
    }

    /**
     * 
     * @return 移除消息队列
     */
    public boolean remove() {
        if (receiptHandle == null)
            return false;
        sess.delete(queue, receiptHandle);
        receiptHandle = null;
        return true;
    }

    /**
     * 创建消息队列
     * 
     * @param queueCode
     *            队列代码
     * @return 返回创建的队列
     */
    public CloudQueue create(String queueCode) {
        return sess.createQueue(queueCode);
    }

    /**
     * 判断消息队列是否存在
     * 
     * @return
     */
    public boolean isExistQueue() {
        return queue.isQueueExist();
    }

    @Override
    public QueueOperator getOperator() {
        if (operator == null)
            operator = new QueueOperator();
        return operator;
    }

    @Override
    public final void setBatchSave(boolean batchSave) {
        super.setBatchSave(batchSave);
        if (!batchSave)
            throw new RuntimeException("QueueQuery.batchSave 不允许为 false");
    }

    public QueueMode getQueueMode() {
        return queueMode;
    }

    public void setQueueMode(QueueMode queueMode) {
        this.queueMode = queueMode;
    }

    public void sessionClose() {
        this.sess.closeSession();
    }
}
