package cn.cerc.jdb.oss;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.jdb.core.StubHandle;

public class OssQueryFind {
    private static final Logger log = LoggerFactory.getLogger(OssQueryFind.class);
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
     * 查询文件
     * 
     * @Description
     * @author rick_zhou
     */
    @Test
    public void queryFile() {
        ds.setOssMode(OssMode.readWrite);
        ds.add("select * from %s", "id_00001.txt");
        ds.open();
    }

}
