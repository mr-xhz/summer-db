package cn.cerc.jdb.core;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CustomDataSetTest_delete {
    private CustomDataSet ds = new CustomDataSet();

    @Before
    public void setUp() throws Exception {
        ds.append().setField("code", "a");
        ds.append().setField("code", "b");
        ds.append().setField("code", "c");
    }

    @Test
    public void test_a() {
        int i = 0;
        ds.first();
        while (!ds.eof()) {
            i++;
            if (ds.getString("code").equals("a"))
                ds.delete();
            else
                ds.next();
        }
        assertEquals(i, 3);
    }

    @Test
    public void test_b() {
        int i = 0;
        ds.first();
        while (!ds.eof()) {
            i++;
            if (ds.getString("code").equals("b"))
                ds.delete();
            else
                ds.next();
        }
        assertEquals(i, 3);
    }

    @Test
    public void test_c() {
        int i = 0;
        ds.first();
        while (!ds.eof()) {
            i++;
            if (ds.getString("code").equals("c"))
                ds.delete();
            else
                ds.next();
        }
        assertEquals(i, 3);
    }
}
