package cn.cerc.jdb.redis;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.junit.Ignore;
import org.junit.Test;

public class RedisBufferTest {

    @Test
    @Ignore
    public void test() {
        RedisBuffer buff = new RedisBuffer();

        Record obj1 = new Record();
        obj1.setCode("中国");
        obj1.setName("中华人民共和国");
        buff.setObject("b1", obj1);

        Record obj2 = (Record) buff.getObject("b1");
        assertEquals(obj1.getName(), obj2.getName());
    }

}

class Record implements Serializable {
    private static final long serialVersionUID = 5328797694692108199L;
    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}