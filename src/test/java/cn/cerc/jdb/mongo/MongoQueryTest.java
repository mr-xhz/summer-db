package cn.cerc.jdb.mongo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.core.DataSet;
import cn.cerc.jdb.core.StubHandle;

public class MongoQueryTest {
    private StubHandle handle;
    private MongoQuery ds;

    @Before
    public void setUp() throws Exception {
        handle = new StubHandle();
        ds = new MongoQuery(handle);
    }

    @Test
    @Ignore
    public void test_open() {
        ds.add("select * from tmp2");
        ds.open();
        System.out.println(ds);
        while (ds.fetch()) {
            // 输出子数据表
            DataSet child = ds.getChildDataSet("data");
            System.out.println(child);
        }
    }

    @Test
    @Ignore
    public void test_append() {
        DataSet data = new DataSet();
        data.append();
        data.setField("it", 1);
        data.setField("month", "201609");
        data.append();
        data.setField("it", 2);
        data.setField("month", "201610");
        //
        ds.add("select * from tmp2 where code='a001'");
        ds.open();
        for (int i = 1; i < 20; i++) {
            ds.append();
            ds.setField("code", "a001");
            ds.setField("value", i);
            ds.setChildDataSet("data", data);
            DataSet ds2 = ds.getChildDataSet("data");
            System.out.println(ds2);
            ds.post();
        }
        ds.append();
        ds.setField("code", "a001QQ");
        ds.setField("value", 30);
        ds.setChildDataSet("data", data);
        DataSet ds2 = ds.getChildDataSet("data");
        System.out.println(ds2);
        ds.post();
    }

    @Test
    @Ignore
    public void test_modify() {
        ds.add("select * from tmp2");
        ds.open();
        while (ds.fetch()) {
            ds.edit();
            ds.setField("value", ds.getInt("value") + 1);
            ds.post();
        }
    }

    @Test
    public void test_select() {
        ds.add("select * from tmp2");
        ds.add("where value<>3 and value<5");
        ds.open();
        System.out.println(ds);
    }

    @Test
    public void test_select2() {
        ds.add("select * from tmp2");
        ds.add("where value<>3 and value<5 and code=a001");
        ds.open();
        System.out.println(ds);
    }

    @Test
    public void test_select3() {
        ds.add("select * from tmp2");
        ds.add("where code like 'QQ'");
        ds.open();
        System.out.println(ds);
    }

    @Test
    @Ignore
    public void test_delete() {
        ds.add("select * from tmp2 where code='a001' and value=3");
        ds.open();
        System.out.println(ds);
        while (!ds.eof())
            ds.delete();
    }
}
