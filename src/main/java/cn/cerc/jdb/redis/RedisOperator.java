package cn.cerc.jdb.redis;

import cn.cerc.jdb.core.IDataOperator;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisOperator implements IDataOperator {

    private RedisSession session;
    public JedisPool jedisPool;
    public Jedis jedis = null;

    public RedisOperator(IHandle handle) {
        session = (RedisSession) handle.getProperty(RedisSession.sessionId);
        jedisPool = session.getJedisPool();
    }

    @Override
    public boolean insert(Record record) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean update(Record record) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean delete(Record record) {
        // TODO Auto-generated method stub
        return false;
    }

}
