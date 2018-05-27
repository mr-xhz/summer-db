package cn.cerc.jdb.redis;

import java.io.IOException;
import java.util.Map;

import cn.cerc.jdb.core.Utils;

public class RedisMap<K, V> {
    private RedisBuffer buffer;
    private String mapKey;

    public RedisMap(String buffKey) {
        this.mapKey = buffKey;
        buffer = new RedisBuffer();
    }

    public RedisBuffer getBuffer() {
        return buffer;
    }

    public void put(K field, V value) {
        buffer.hset(mapKey, "" + field, "" + value);
    }

    public String get(K field) {
        return buffer.hget(mapKey, "" + field);
    }

    public void putObject(K field, Object obj) {
        try {
            buffer.hset(mapKey, "" + field, Utils.serializeToString(obj));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Object getObject(K field) {
        String str = buffer.hget(mapKey, "" + field);
        if (str != null)
            try {
                return Utils.deserializeToObject(str);
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        else
            return null;
    }

    public void remove(K field) {
        buffer.hdel(mapKey, "" + field);
    }

    public long len() {
       return buffer.hlen(mapKey);
    }
    
    public Map<String, String> getItems() {
        return buffer.hgetAll(mapKey);
    }

}
