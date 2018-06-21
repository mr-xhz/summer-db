package cn.cerc.jdb.oss;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.jdb.core.StubHandle;

public class OssQuerySend {
    private static final Logger log = LoggerFactory.getLogger(OssQuerySend.class);
    private static OssQuery ds;
    private static StubHandle handle;

    @BeforeClass
    public static void setUp() {
        handle = new StubHandle();
        ds = new OssQuery(handle);
    }

    @AfterClass
    public static void closeSession() {
        handle.closeConnections();
    }

    /**
     * 保存文件/覆盖文件
     * 
     */
    @Test
    public void saveFile() {
        ds.setOssMode(OssMode.create);
        ds.add("select * from %s", "id_00001.txt");
        ds.setOssMode(OssMode.readWrite);
        ds.open();
        ds.append();
        ds.setField("num", ds.getInt("num") + 1);
        ds.save();
    }

}
