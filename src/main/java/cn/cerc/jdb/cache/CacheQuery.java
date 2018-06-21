package cn.cerc.jdb.cache;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.IRecord;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.core.TDate;
import cn.cerc.jdb.core.TDateTime;

public class CacheQuery implements IRecord {
    private static final Logger log = Logger.getLogger(CacheQuery.class);

    private String key;
    private boolean existsData = false;
    private int expires = 3600; // 单位：秒

    private Record record = new Record();
    private boolean modified = false;

    // 缓存对象
    private IMemcache cache;
    private boolean connected;

    public CacheQuery(IMemcache cache) {
        this.cache = cache;
    }

    public CacheQuery setKey(String key) {
        if (this.key != null)
            throw new RuntimeException("[CacheQuery]错误的初始化参数！");
        if (key == null)
            throw new RuntimeException("[CacheQuery]错误的初始化参数！");
        this.key = key;

        connected = true;
        existsData = false;
        String recordStr = (String) cache.get(key);
        log.debug("cache get:" + key.toString() + ":" + recordStr);
        if (recordStr != null && !"".equals(recordStr)) {
            try {
                record.setJSON(recordStr);
                existsData = true;
            } catch (Exception e) {
                log.error("缓存数据格式有误：" + recordStr, e);
                e.printStackTrace();
            }
        }
        return this;
    }

    public final void post() {
        if (cache != null && this.modified) {
            try {
                cache.set(key, record.toString(), this.expires);
                log.debug("cache set:" + key.toString() + ":" + record.toString());
                this.modified = false;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

    }

    public boolean isNull() {
        return !this.existsData;
    }

    public String getKey() {
        return key;
    }

    public void clear() {
        if (this.existsData) {
            // log.info("cache delete:" + key.toString());
            cache.delete(key);
            this.modified = false;
            this.existsData = false;
        }
        record.clear();
        record.getFieldDefs().clear();
    }

    public boolean hasValue(String field) {
        return !isNull() && getString(field) != null && !"".equals(getString(field)) && !"{}".equals(getString(field));
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public boolean Connected() {
        return connected;
    }

    @Override
    public boolean getBoolean(String field) {
        return record.getBoolean(field);
    }

    @Override
    public int getInt(String field) {
        return record.getInt(field);
    }

    @Override
    public double getDouble(String field) {
        return record.getDouble(field);
    }

    @Override
    public String getString(String field) {
        return record.getString(field);
    }

    @Override
    public TDate getDate(String field) {
        return record.getDate(field);
    }

    @Override
    public TDateTime getDateTime(String field) {
        return record.getDateTime(field);
    }

    @Override
    public IRecord setField(String field, Object value) {
        this.modified = true;
        record.setField(field, value);
        return this;
    }

    public void setNull(String field) {
        setField(field, null);
    }

    @Override
    public String toString() {
        if (record != null)
            return record.toString();
        else
            return null;
    }

    public Record getRecord() {
        return this.record;
    }

    @Override
    public boolean exists(String field) {
        return record.exists(field);
    }

    @Override
    public Object getField(String field) {
        return record.getField(field);
    }

    protected IMemcache getCache() {
        return cache;
    }

    protected void setCache(IMemcache cache) {
        this.cache = cache;
    }

    @Override
    public BigInteger getBigInteger(String field) {
        return record.getBigInteger(field);
    }

    @Override
    public BigDecimal getBigDecimal(String field) {
        return record.getBigDecimal(field);
    }
}
