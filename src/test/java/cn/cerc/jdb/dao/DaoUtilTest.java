package cn.cerc.jdb.dao;

import org.junit.Test;

import cn.cerc.jdb.core.StubHandle;

public class DaoUtilTest {

    @Test
    public void testBuildEntity() {
        StubHandle handle = new StubHandle();
        String text = DaoUtil.buildEntity(handle, "t_profitday", "ProfitDay");
        System.out.println(text);
    }
}
