package cn.cerc.jdb.redis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.Utils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisBuffer {
    private static final Logger log = Logger.getLogger(RedisBuffer.class);

    // redis 数据库编号
    private int dbIndex = 0;

    public boolean isConnected() {
        return execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean call(Jedis jedis, Object params) {
                return jedis.isConnected();
            }
        }, dbIndex);
    }

    public Boolean exists(String key) {
        return execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean call(Jedis jedis, Object params) {
                String key = ((Object[]) params)[1].toString();
                return jedis.exists(key);
            }
        }, dbIndex, key);
    }

    public Long hlen(String key) {
        return execute(new RedisCallback<Long>() {
            @Override
            public Long call(Jedis jedis, Object params) {
                String key = ((Object[]) params)[1].toString();
                return jedis.hlen(key);
            }
        }, dbIndex, key);
    }

    public Boolean hexists(String mapKey, String attributeKey) {
        return execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean call(Jedis jedis, Object params) {
                String key = ((Object[]) params)[1].toString();
                String field = ((Object[]) params)[2].toString();
                return jedis.hexists(key, field);
            }
        }, dbIndex, mapKey, attributeKey);
    }

    public String hget(String key, String field) {
        return execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                String field = ((Object[]) parms)[2].toString();
                return jedis.hget(key, field);
            }
        }, dbIndex, key, field);
    }

    public Map<String, String> hgetAll(String key) {
        return execute(new RedisCallback<Map<String, String>>() {
            @Override
            public Map<String, String> call(Jedis jedis, Object params) {
                String key = ((Object[]) params)[1].toString();
                return jedis.hgetAll(key);
            }
        }, dbIndex, key);
    }

    public Long hdel(String mapKey, String attributeKey) {
        return execute(new RedisCallback<Long>() {
            @Override
            public Long call(Jedis jedis, Object params) {
                String key = ((Object[]) params)[1].toString();
                String field = ((Object[]) params)[2].toString();
                return jedis.hdel(key, field);
            }
        }, dbIndex, mapKey, attributeKey);
    }

    public void hset(String key, String field, String value) {
        execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                String field = ((Object[]) parms)[2].toString();
                String value = ((Object[]) parms)[3].toString();
                jedis.hset(key, field, value);
                return null;
            }
        }, dbIndex, key, field, value);
    }

    public String get(String key) {
        return execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                return jedis.get(key);
            }
        }, dbIndex, key);
    }

    public byte[] getByte(String key) {
        return execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                try {
                    return jedis.get(key.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }
        }, dbIndex, key);
    }

    public void set(String key, String value) {
        execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                String value = ((Object[]) parms)[2].toString();
                jedis.set(key, value);
                return null;
            }
        }, dbIndex, key, value);
    }

    public void set(String key, byte[] value) {
        execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                byte[] value = (byte[]) ((Object[]) parms)[2];
                try {
                    jedis.set(key.getBytes("UTF-8"), value);
                } catch (UnsupportedEncodingException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }
        }, dbIndex, key, value);
    }

    public void set(String key, String value, int seconds) {
        execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                String value = ((Object[]) parms)[2].toString();
                String seconds = ((Object[]) parms)[3].toString();
                jedis.setex(key, Integer.parseInt(seconds), value);
                return null;
            }
        }, dbIndex, key, value, seconds);
    }

    public void set(String key, byte[] value, int seconds) {
        execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                byte[] value = (byte[]) ((Object[]) parms)[2];
                String seconds = ((Object[]) parms)[3].toString();
                try {
                    jedis.setex(key.getBytes("UTF-8"), Integer.parseInt(seconds), value);
                } catch (UnsupportedEncodingException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }
        }, dbIndex, key, value, seconds);
    }

    public void setObject(String key, Object obj) {
        try {
            set(key, Utils.serializeToString(obj));
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public Object getObject(String key) {
        String str = get(key);
        try {
            return str == null ? null : Utils.deserializeToObject(str);
        } catch (ClassNotFoundException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public void del(String key) {
        execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                jedis.del(key);
                return null;
            }
        }, dbIndex, key);
    }

    public String llen(String key) {
        return execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                return jedis.llen(key) + "";
            }
        }, dbIndex, key);
    }

    public void lpush(String key, String value) {
        execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                String value = ((Object[]) parms)[2].toString();
                jedis.lpush(key, value);
                return null;
            }
        }, dbIndex, key, value);
    }

    public void lpushPipeLine(String key, List<String> values) {
        execute(new RedisCallback<String>() {
            @Override
            @SuppressWarnings("unchecked")
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                List<String> values = (List<String>) ((Object[]) parms)[2];
                Pipeline p = jedis.pipelined();
                for (String value : values) {
                    p.lpush(key, value);
                }
                p.sync();// 同步
                return null;
            }
        }, dbIndex, key, values);
    }

    public List<String> lrange(String key, long start, long end) {
        return execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> call(Jedis jedis, Object parms) {
                Object[] ps = ((Object[]) parms);
                String key = ps[1].toString();
                long start = Long.parseLong(ps[2].toString());
                long end = Long.parseLong(ps[3].toString());
                return jedis.lrange(key, start, end);
            }
        }, dbIndex, key, start, end);
    }

    public void incr(String key) {
        execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                jedis.incr(key);
                return null;
            }
        }, dbIndex, key);
    }

    public void sadd(String key, String value) {
        execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                String key = ((Object[]) parms)[1].toString();
                String value = ((Object[]) parms)[2].toString();
                jedis.sadd(key, value);
                return null;
            }
        }, dbIndex, key, value);
    }

    public Set<String> smembers(String key) {
        return execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> call(Jedis jedis, Object parms) {
                Object[] ps = ((Object[]) parms);
                String key = ps[1].toString();
                return jedis.smembers(key);
            }
        }, dbIndex, key);
    }

    public List<String> brpop(String key) {
        return execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> call(Jedis jedis, Object parms) {
                Object[] ps = ((Object[]) parms);
                String key = ps[1].toString();
                return jedis.brpop(0, key);
            }
        }, dbIndex, key);
    }

    // index代表 缓存存放到哪一个db实例里面
    protected <T> T execute(RedisCallback<T> callback, Object... args) {
        Jedis jedis = null;
        try {
            Object index = args[0];
            if (null != index && Integer.parseInt(index.toString()) > 0 && Integer.parseInt(index.toString()) < 16) {
                jedis = getRedis(Integer.parseInt(index.toString()));
            } else {
                jedis = getRedis();
            }
            return callback.call(jedis, args);
        } catch (JedisConnectionException e) {
            if (jedis != null)
                jedis.close();
            jedis = getRedis();
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public void select(int dbIndex) {
        execute(new RedisCallback<String>() {
            @Override
            public String call(Jedis jedis, Object parms) {
                int index = Integer.parseInt(((Object[]) parms)[0].toString());
                return jedis.select(index);
            }
        }, dbIndex);
    }

    @Deprecated // 请改使用 RedisFactory.getJedis
    public Jedis getRedis(int index) {
        Jedis jedis = JedisFactory.getJedis();
        jedis.select(index);
        return jedis;
    }

    @Deprecated // 请改使用 RedisFactory.getJedis
    public Jedis getRedis() {
        return this.getRedis(0);
    }
}
