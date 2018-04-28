package cn.cerc.jdb.dao;

import org.junit.Test;

import cn.cerc.jdb.core.StubHandle;

public class DaoUtilTest {

    @Test
    public void testBuildEntity() {
        StubHandle handle = new StubHandle();
        String text = DaoUtil.buildEntity(handle, "s_userinfo", "UserInfo");
        System.out.println(text);
    }
}
