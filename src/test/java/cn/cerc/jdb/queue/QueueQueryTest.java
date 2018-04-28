package cn.cerc.jdb.queue;

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.jdb.core.StubHandle;
import cn.cerc.jdb.mongo.MongoSession;

public class QueueQueryTest {
    private static final Logger log = LoggerFactory.getLogger(MongoSession.class);

    private static QueueQuery dataSet;
    private static StubHandle handle;

    @BeforeClass
    public static void setUp() {
        handle = new StubHandle();
        dataSet = new QueueQuery(handle);
    }

    @AfterClass
    public static void closeSession() {
        dataSet.sessionClose();
    }

    /**
     * 发送消息
     * 
     * @Description
     * @author rick_zhou
     */
    @Test
    public void sendMsg() {
        // ds.add("select * from %s", appdb.get(handle, appdb.MQ_TOPIC_NAME));
        dataSet.add("select * from %s", QueueDB.TEST);
        dataSet.open();

        // append head
        dataSet.getHead().setField("queueHeadData1", "queueHeadData1");
        dataSet.getHead().setField("queueHeadData2", "queueHeadData2");
        dataSet.getHead().setField("queueHeadData3", "queueHeadData3");
        dataSet.getHead().setField("queueHeadData4", "queueHeadData4");

        // append body
        dataSet.append();
        dataSet.setField("queueBodyData1", "queueBodyData1");
        dataSet.setField("queueBodyData2", "queueBodyData2");
        dataSet.setField("queueBodyData3", "queueBodyData3");
        dataSet.setField("queueBodyData4", "queueBodyData4");
        dataSet.setField("queueBodyData5", "queueBodyData5");
        dataSet.setQueueMode(QueueMode.append);
        dataSet.save();
    }

    /**
     * 获取一个队列中的一条消息(注意!这条消息有可能会被消费多次)
     * 
     * @Description
     * @author rick_zhou
     */
    @Test
    public void query() {
        // ds.add("select * from %s", appdb.get(handle, appdb.MQ_TOPIC_NAME));
        dataSet.add("select * from %s", QueueDB.TEST);
        dataSet.setQueueMode(QueueMode.recevie);
        dataSet.open();// 获取消息

        // 获取消息中DataSet的head内容
        log.info(StringUtils.center("heand data", 70, "=="));

        log.info(StringUtils.center("body data", 70, "=="));

        dataSet.remove();
    }

}
