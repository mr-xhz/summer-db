package cn.cerc.jdb.mysql;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.core.PostFieldException;
import cn.cerc.jdb.core.StubHandle;
import cn.cerc.jdb.core.TDateTime;

public class SqlQueryTest_post1 {
    private SqlQuery ds;
    private StubHandle conn;

    @Before
    public void setUp() {
        conn = new StubHandle();
        ds = new SqlQuery(conn);
    }

    @Test(expected = PostFieldException.class)
    @Ignore(value = "仅允许在测试数据库运行")
    public void post_error() {
        ds.getFieldDefs().add("Test");
        ds.add("select * from Dept where CorpNo_='%s'", "144001");
        ds.open();
        ds.edit();
        ds.setField("updateDate_", TDateTime.Now().incDay(-1));
        ds.post();
    }

    @Test()
    @Ignore(value = "仅允许在测试数据库运行")
    public void post() {
        ds.add("select * from Dept where CorpNo_='%s'", "144001");
        ds.open();
        ds.setOnBeforePost(ds -> {
            System.out.println("before post");
        });
        ds.edit();
        ds.setField("Test", "aOK");
        ds.setField("UpdateDate_", TDateTime.Now().incDay(-1));
        ds.post();
    }
}
