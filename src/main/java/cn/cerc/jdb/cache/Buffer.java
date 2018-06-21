package cn.cerc.jdb.cache;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.ServerConfig;

public class Buffer extends CacheQuery {

    public Buffer() {
        super(getMemcache());
    }

    public Buffer(Class<?> clazz) {
        super(getMemcache());
        this.setKey(clazz.getName());
    }

    public Buffer(Object... keys) {
        super(getMemcache());
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < keys.length; i++) {
            if (i > 0)
                str.append(".");
            str.append(keys[i]);
        }
        setKey(str.toString());
    }

    public static IMemcache getMemcache() {
        CacheConnection conn = new CacheConnection();
        conn.setConfig(new IConfig() {

            @Override
            public String getProperty(String key, String def) {
                return ServerConfig.getInstance().getProperty(key, def);
            }

            @Override
            public String getProperty(String key) {
                return ServerConfig.getInstance().getProperty(key);
            }

        });
        return conn.getSession();
    }
}
