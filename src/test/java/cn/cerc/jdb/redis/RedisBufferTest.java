package cn.cerc.jdb.redis;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Ignore;
import org.junit.Test;

public class RedisBufferTest {

    @Test
    @Ignore
    public void test() {
        RedisBuffer buff = new RedisBuffer();
        buff.hset("test", "1.0", "a");
        buff.hset("test", "20.0", "b");
        buff.hset("test", "10.5", "c");
        buff.hset("test", "3.0", "d");
        Map<String, String> items = buff.hgetAll("test");
        System.err.println(items);
        TreeMap<String, String> args = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Double v1 = Double.parseDouble(o1);
                Double v2 = Double.parseDouble(o2);
                return v1.compareTo(v2);
            }
        });
        args.putAll(items);
        System.out.println(args);
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