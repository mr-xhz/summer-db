package cn.cerc.jdb.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.IConnection;

public class MongoConnection implements IConnection {
    private static final Logger log = LoggerFactory.getLogger(MongoConnection.class);
    private static MongoClient pool;
    private static String dbname;
    private IConfig config;

    @Override
    public void setConfig(IConfig config) {
        this.config = config;
    }

    public IConfig getConfig() {
        return config;
    }

    @Override
    public MongoSession getSession() {
        init();
        MongoDatabase database = pool.getDatabase(dbname);
        MongoSession sess = new MongoSession();
        sess.setDatabase(database);
        return sess;
    }

    @Override
    public void init() {
        if (MongoConnection.pool == null) {
            dbname = config.getProperty(MongoSession.mgdb_dbname);
            StringBuffer sb = new StringBuffer();
            sb.append("mongodb://");
            // userName
            sb.append(config.getProperty(MongoSession.mgdb_username));
            // password
            sb.append(":").append(config.getProperty(MongoSession.mgdb_password));
            // ip
            sb.append("@").append(config.getProperty(MongoSession.mgdb_site));
            // database
            sb.append("/").append(config.getProperty(MongoSession.mgdb_dbname));

            if ("true".equals(config.getProperty(MongoSession.mgdb_enablerep))) {
                // replacaset
                sb.append("?").append("replicaSet=").append(config.getProperty(MongoSession.mgdb_replicaset));
                // poolsize
                sb.append("&").append("maxPoolSize=").append(config.getProperty(MongoSession.mgdb_maxpoolsize));
                log.info("连接到MongoDB分片集群:" + sb.toString());
                // MongoClientURI connectionString = new MongoClientURI(
                // "mongodb://ehealth:123456@115.28.67.211:3717,115.28.67.211:13717/ehealth?replicaSet=mgset-2004675");
            }
            MongoClientURI connectionString = new MongoClientURI(sb.toString());
            pool = new MongoClient(connectionString);
        }
    }
}