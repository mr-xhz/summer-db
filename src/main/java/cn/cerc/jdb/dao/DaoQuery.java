package cn.cerc.jdb.dao;

import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.mysql.SqlQuery;

public class DaoQuery<T> extends SqlQuery {
    private static final long serialVersionUID = -7323397340337332570L;
    private Class<T> clazz;

    public DaoQuery(IHandle handle, Class<T> clazz) {
        super(handle);
        this.clazz = clazz;
        this.add("select * from " + this.getTableName());
    }

    /** 将对象追加到数据表中 */
    public void append(T item) {
        this.append();
        DaoUtil.copy(item, this.getCurrent());
        this.post();
    }

    /** 与read函数配套，将对象内容保存到数据库中 */
    public void save(T item) {
        this.edit();
        DaoUtil.copy(item, this.getCurrent());
        this.post();
    }

    public T read() {
        T obj = null;
        try {
            obj = this.clazz.newInstance();
            Record record = this.getCurrent();
            DaoUtil.copy(record, obj);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public String getTableName() {
        return DaoUtil.getTableName(clazz);
    }

}
