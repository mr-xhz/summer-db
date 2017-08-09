package cn.cerc.jdb.mssql;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IDataOperator;
import cn.cerc.jdb.core.IHandle;

public class MssQuery extends DataQuery {
    private static final long serialVersionUID = 889285738942368226L;

    public MssQuery(IHandle handle) {
        super(handle);
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
