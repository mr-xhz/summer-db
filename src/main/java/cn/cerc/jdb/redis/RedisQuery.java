package cn.cerc.jdb.redis;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IDataOperator;
import cn.cerc.jdb.core.IHandle;

public class RedisQuery extends DataQuery {

    private static final long serialVersionUID = 5655647321461069483L;
    public static RedisSession sess;

    public RedisQuery(IHandle handle) {
        super(handle);
        sess = (RedisSession) handle.getProperty(RedisSession.sessionId);
    }

    @Override
    public DataQuery open() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void save() {
        // TODO Auto-generated method stub

    }

    @Override
    public IDataOperator getOperator() {
        // TODO Auto-generated method stub
        return null;
    }

}
