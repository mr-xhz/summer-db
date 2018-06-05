package cn.cerc.jdb.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConfig implements IConfig {
    private static final Logger log = LoggerFactory.getLogger(ServerConfig.class);
    private static Properties properties = new Properties();
    private static ServerConfig instance;
    // 是否为debug状态
    private int debug = -1;

    private static final String confFile = "/application.properties";
    static {
        try {
            InputStream file = ServerConfig.class.getResourceAsStream(confFile);
            if (file != null) {
                properties.load(file);
                log.info("read from file: " + confFile);
            } else {
                log.error("not find file: " + confFile);
            }
        } catch (FileNotFoundException e) {
            log.error("The settings file '" + confFile + "' does not exist.");
        } catch (IOException e) {
            log.error("Failed to load the settings from the file: " + confFile);
        }
    }

    public ServerConfig() {
        if (instance != null) {
            log.error("ServerConfig instance is not null");
        }
        instance = this;
    }

    public static ServerConfig getInstance() {
        if (instance == null) {
            new ServerConfig();
        }
        return instance;
    }

    @Override
    public String getProperty(String key, String def) {
        String result = null;
        LocalConfig config = LocalConfig.getInstance();
        result = config.getProperty(key, null);
        if (result == null) {
            if (properties != null)
                result = properties.getProperty(key);
        }
        return result != null ? result : def;
    }

    @Override
    public String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * 
     * @return 返回当前是否为debug状态
     */
    public boolean isDebug() {
        if (debug == -1) {
            debug = "1".equals(this.getProperty("debug", "0")) ? 1 : 0;
        }
        return debug == 1;
    }
}
