package cn.cerc.jdb.mysql;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.jdb.core.StubHandle;

public class SqlQueryTest {
    private static final Logger log = LoggerFactory.getLogger(SqlQueryTest.class);
    private static final String Account = "Account";
    private SqlQuery ds;
    private StubHandle handle;

    @Before
    public void setUp() {
        handle = new StubHandle();
        ds = new SqlQuery(handle);
    }

    @Test
    public void test_open() {
        ds.setMaximum(1);
        ds.add("select Code_,Name_ from %s", Account);
        ds.open();
        assertEquals(ds.size(), 1);
    }

    @Test
    public void test_open_getSelectCommand() {
        String sql;
        sql = String.format("select Code_,Name_ from %s", Account);
        ds.add(sql);
        assertEquals(String.format("%s limit %s", sql, String.valueOf(BigdataException.MAX_RECORDS + 2)),
                ds.getSelectCommand());

        ds.clear();
        sql = String.format("select Code_,Name_ from %s limit 1", Account);
        ds.setMaximum(-1);
        ds.add(sql);
        assertEquals(sql, ds.getSelectCommand());

        ds.clear();
        ds.setMaximum(BigdataException.MAX_RECORDS);
        sql = String.format("select Code_,Name_ from %s", Account);
        ds.add(sql);

        assertEquals(String.format("%s limit %s", sql, String.valueOf(BigdataException.MAX_RECORDS + 2)),
                ds.getSelectCommand());
    }

    @Test
    public void test_open_2() {
        ds.setMaximum(1);
        String str = "\\小王233\\";
        ds.add("select * from %s where Name_='%s'", Account, str);
        log.info(ds.getCommandText());
        ds.open();

        ds.clear();
        str = "\\\\小王233\\\\";
        ds.add("select * from %s where Name_='%s'", Account, str);
        log.info(ds.getCommandText());
        ds.open();

    }

}
