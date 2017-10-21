package cn.cerc.jdb.redis;

public class RedisKVPO {
    private String key;
    private String value;

    public RedisKVPO() {
    }

    public RedisKVPO(String k, String v) {
        this.key = k;
        this.value = v;
    }

    public String getK() {
        return key;
    }

    public void setK(String k) {
        this.key = k;
    }

    public String getV() {
        return value;
    }

    public void setV(String v) {
        this.value = v;
    }

}