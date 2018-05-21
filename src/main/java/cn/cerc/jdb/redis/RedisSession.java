package cn.cerc.jdb.redis;

import cn.cerc.jdb.core.ISession;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisSession implements ISession {
    public static final String redis_site = "redis.site";
    public static final String redis_port = "redis.port";
    public static final String redis_password = "redis.password";
    public static final String sessionId = "redisSession";
    private Jedis redis;
    private JedisPool jedisPool;

    public RedisSession() {
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void closeSession() {
        if (redis != null) {
            redis = null;
        }
    }

    public void destory() {
        jedisPool.destroy();
    }

    public Jedis getRedis(int index) {
        Jedis jedis = jedisPool.getResource();
        jedis.select(index);
        return jedis;
    }

    @Deprecated
    public void returnRedis(Jedis jedis) {
        jedisPool.returnResource(jedis);
    }

    @Deprecated
    public void returnBrokeRedis(Jedis jedis) {
        jedisPool.returnBrokenResource(jedis);
    }

    public Jedis getRedis() {
        Jedis jedis = jedisPool.getResource();
        jedis.select(0);
        return jedis;
    }

}
