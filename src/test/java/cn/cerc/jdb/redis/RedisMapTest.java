package cn.cerc.jdb.redis;

import java.util.Comparator;
import java.util.TreeMap;

import org.junit.Ignore;
import org.junit.Test;

public class RedisMapTest {

    @Test
    @Ignore
    public void test() {
        RedisMap<Double, Double> map = new RedisMap<>("test");
        map.put(1.0, 1.0);
        map.put(3.0, 2.0);
        map.put(2.0, 3.0);
        map.put(10.0, 4.0);
        map.put(20.0, 5.0);
        map.put(10.1, 6.0);
        // map.remove(10.1);
        TreeMap<String, String> items = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Double v1 = Double.parseDouble(o1);
                Double v2 = Double.parseDouble(o2);
                return v1.compareTo(v2);
            }
        });
        items.putAll(map.getItems());
        System.out.println(items);
    }

}
