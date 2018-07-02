package cn.cerc.jdb.redis;

import java.io.Closeable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.TDateTime;
import redis.clients.jedis.Jedis;

public class Locker implements Closeable {
    private static Logger log = Logger.getLogger(Locker.class);
    private String group;
    private String message;
    private Map<String, Boolean> items = new HashMap<>();
    private int timeout = 10000; // 锁超时时间，默认10秒

    public Locker(String group, Object first, Object... args) {
        this.group = group;
        items.put(group + "-" + first, false);
        for (Object arg : args)
            items.put(group + "-" + arg, false);
    }

    public void add(Object key) {
        items.put(group + "-" + key, false);
    }

    public boolean lock(String flag) {
        return lock(flag, 100);
    }

    public boolean lock(String flag, int time) {
        if (time % 100 != 0)
            throw new RuntimeException(String.format("%s %% 100 !=0", time));
        if (items.size() == 0)
            items.put(group, false);
        try (Jedis jedis = JedisFactory.getJedis()) {
            for (String key : items.keySet()) {
                // System.out.println("key: " + key);
                if (!tryLock(jedis, key, flag, time / 100)) {
                    log.info(this.message);
                    return false;
                }
                items.put(key, true);
            }
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean tryLock(Jedis jedis, String key, String flag, int num) throws InterruptedException {
        boolean result = false;
        int i = 0;
        while (i < num) {
            i++;
            long curTime = System.currentTimeMillis() + timeout;
            if (jedis.setnx(key, curTime + "," + flag) == 1) {
                this.message = String.format("[%s]%s锁定成功", key, flag);
                result = true;
                break;
            } else {
                String currentValue = jedis.get(key);
                if (currentValue != null && currentValue.split(",").length == 2) {
                    String args[] = currentValue.split(",");
                    long lastTime = Long.parseLong(args[0]);
                    if (System.currentTimeMillis() > lastTime) {
                        String oldValue = jedis.getSet(key, curTime + "," + flag);
                        if (oldValue != null && oldValue.equals(currentValue)) {
                            // System.out.println("lastTime: " + lastTime);
                            // System.out.println(" curTime: " + curTime);
                            this.message = String.format("[%s]%s强制锁定成功", key, flag);
                            // System.out.println(this.message);
                            result = true;
                            break;
                        }
                    }
                    TDateTime tmp = new TDateTime(new Date(lastTime));
                    this.message = String.format("[%s]%s锁定失败， %s完成后(%s)再试", key, flag, args[1], tmp.getTime());
                } else {
                    this.message = String.format("[%s]%s锁定失败， %s完成后再试", key, flag, currentValue);
                }
            }
            if (i < num) {
                // System.out.println("try " + (i + 1));
                Thread.sleep(100);
            }
        }
        log.debug(this.message);
        return result;
    }

    @Override
    public void close() {
        try (Jedis jedis = JedisFactory.getJedis()) {
            for (String key : items.keySet()) {
                if (items.get(key))
                    jedis.del(key);
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
