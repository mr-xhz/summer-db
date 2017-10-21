package cn.cerc.jdb.redis;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.IConnection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnection implements IConnection {
    private static final Logger log = Logger.getLogger(RedisConnection.class);
    // Redis服务器IP
    private static String ADDR = "127.0.0.1";
    // Redis的端口号
    private static int PORT = 6379;
    // 访问密码
    private static String AUTH = "123456";
    // 可用连接实例的最大数目，默认值为8；
    // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 1024;
    // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 200;
    // 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;

    private static int TIMEOUT = 10000;

    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;
    private static JedisPool jedisPool;
    private IConfig config;

    @Override
    public void setConfig(IConfig config) {
        this.config = config;
    }

    public IConfig getConfig() {
        return config;
    }

    /**
     * 初始化jedis连接池
     */
    @Override
    public void init() {
        if (jedisPool == null) {
            try {
                JedisPoolConfig jconfig = new JedisPoolConfig();
                jconfig.setMaxTotal(MAX_ACTIVE);
                jconfig.setMaxIdle(MAX_IDLE);
                jconfig.setMaxWaitMillis(MAX_WAIT);
                jconfig.setTestOnBorrow(TEST_ON_BORROW);
                jconfig.setTestOnBorrow(true);
                jconfig.setTestOnReturn(true);
                // Idle时进行连接扫描
                jconfig.setTestWhileIdle(true);
                // 表示idle object evitor两次扫描之间要sleep的毫秒数
                jconfig.setTimeBetweenEvictionRunsMillis(30000);
                // 表示idle object evitor每次扫描的最多的对象数
                jconfig.setNumTestsPerEvictionRun(10);
                // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object
                // evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
                jconfig.setMinEvictableIdleTimeMillis(60000);
                ADDR = config.getProperty(RedisSession.redis_site, "127.0.0.1");// ip
                PORT = Integer.parseInt(config.getProperty(RedisSession.redis_port, "6379"));// 端口号
                AUTH = config.getProperty(RedisSession.redis_password, "123456");// 密码
                // 建立连接池
                jedisPool = new JedisPool(jconfig, ADDR, PORT, TIMEOUT, AUTH);
                boolean connected = isConnected();
                if (!connected) {
                    log.error("redis 初始化出错 缓存服务器连接不上！ ");
                    throw new Exception("IP:" + PORT + ", redis服务器不可以连接~~~，请检查配置 与redis 服务器");
                }
                log.info("连接到redis:" + ADDR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public RedisSession getSession() {
        init();
        RedisSession sess = new RedisSession();
        sess.setJedisPool(jedisPool);
        return sess;
    }

    public boolean isConnected() {
        return getRedis().isConnected();
    }

    public Jedis getRedis() {
        Jedis jedis = jedisPool.getResource();
        jedis.select(0);
        return jedis;
    }

    public static void main(String[] args) {
        /*
         * Jedis jedis = new Jedis("localhost",6379);
         * jedis.auth("123456");//设置密码 System.out.println(
         * "Connection to server sucessfully"); //check whether server is
         * running or not System.out.println("Server is running: "
         * +jedis.ping());
         */
        /*
         * JedisPoolConfig jconfig = new JedisPoolConfig();
         * jconfig.setMaxTotal(MAX_ACTIVE); jconfig.setMaxIdle(MAX_IDLE);
         * jconfig.setMaxWaitMillis(MAX_WAIT);
         * jconfig.setTestOnBorrow(TEST_ON_BORROW); jedisPool = new
         * JedisPool(jconfig, ADDR, PORT, TIMEOUT, AUTH);
         */
        /*
         * JedisPoolConfig config = new JedisPoolConfig();
         * config.setMaxTotal(200); config.setMaxIdle(50);
         * config.setMinIdle(8);//设置最小空闲数 config.setMaxWaitMillis(10000);
         * config.setTestOnBorrow(true); config.setTestOnReturn(true);
         * //Idle时进行连接扫描 config.setTestWhileIdle(true); //表示idle object
         * evitor两次扫描之间要sleep的毫秒数
         * config.setTimeBetweenEvictionRunsMillis(30000); //表示idle object
         * evitor每次扫描的最多的对象数 config.setNumTestsPerEvictionRun(10);
         * //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object
         * evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
         * config.setMinEvictableIdleTimeMillis(60000); jedisPool = new
         * JedisPool(config, "127.0.0.1", 6379, TIMEOUT, "123456");
         * log.info("连接到redis:" +ADDR); Jedis redis = jedisPool.getResource();
         * System.out.println(redis.clientList());
         */
    }
}
