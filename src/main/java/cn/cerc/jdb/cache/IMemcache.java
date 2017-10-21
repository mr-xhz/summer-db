package cn.cerc.jdb.cache;

public interface IMemcache {
    void set(String key, Object value); // 默认3600秒

    void set(String key, Object value, int expires); // 指定过期时间

    Object get(String key);

    void delete(String key);
}
