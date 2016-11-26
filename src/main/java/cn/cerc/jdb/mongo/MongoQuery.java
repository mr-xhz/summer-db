package cn.cerc.jdb.mongo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.DataSet;
import cn.cerc.jdb.core.DataSetState;
import cn.cerc.jdb.core.IDataOperator;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.mysql.SqlOperator;
import cn.cerc.jdb.other.utils;

public class MongoQuery extends DataQuery {
	private static final long serialVersionUID = -1262005194419604476L;
	private MongoSession session = null;
	// 数据库保存操作执行对象
	private IDataOperator operator;
	// 仅当batchSave为true时，delList才有记录存在
	private List<Record> delList = new ArrayList<>();

	public MongoQuery(IHandle handle) {
		super(handle);
		session = (MongoSession) this.handle.getProperty(MongoSession.sessionId);
	}

	@Override
	public DataQuery open() {
		String table = SqlOperator.findTableName(this.getCommandText());
		// 查找业务ID对应的数据
		MongoCollection<Document> coll = session.getDatabase().getCollection(table);
		// 增加查询条件
		BasicDBObject filter = decodeWhere(this.getCommandText());
		// 增加排序条件
		BasicDBObject sort = decodeOrder(this.getCommandText());
		// 执行查询
		ArrayList<Document> list = coll.find(filter).sort(sort).into(new ArrayList<Document>());
		// 数据不存在,则状态不为更新,并返回一个空数据
		if (list == null || list.isEmpty())
			return this;

		for (Document doc : list) {
			Record record = append().getCurrent();
			for (String field : doc.keySet()) {
				if ("_id".equals(field)) {
					Object uid = doc.get(field);
					record.setField(field, uid != null ? uid.toString() : uid);
				} else
					record.setField(field, doc.get(field));
			}
			record.setState(DataSetState.dsNone);
		}
		this.first();
		this.active = true;
		return this;
	}

	// 将sql指令查询条件改为MongoDB格式
	protected BasicDBObject decodeWhere(String sql) {
		BasicDBObject filter = new BasicDBObject();
		int offset = sql.toLowerCase().indexOf("where");
		if (offset > -1) {
			int endIndex = sql.toLowerCase().indexOf("order");
			String[] items;
			if (endIndex > -1)
				items = sql.substring(offset + 5, endIndex).split("and");
			else
				items = sql.substring(offset + 5).split("and");
			for (String item : items) {
				if (item.split("=").length == 2) {
					String[] tmp = item.split("=");
					String field = tmp[0].trim();
					String value = tmp[1].trim();
					if (value.startsWith("'") && value.endsWith("'"))
						filter.append(field, value.substring(1, value.length() - 1));
					else if (utils.isNumeric(value))
						filter.append(field, Double.parseDouble(value));
					else
						filter.append(field, value);
				} else
					throw new RuntimeException("暂不支持的查询条件：" + item);
			}
		}
		return filter;
	}

	// 将sql指令排序条件改为MongoDB格式
	protected BasicDBObject decodeOrder(String sql) {
		BasicDBObject sort = new BasicDBObject();
		int offset = sql.toLowerCase().indexOf("order");
		if (offset == -1)
			return sort;
		String[] items = sql.substring(offset + 9).split(",");
		for (String item : items) {
			String str = item.trim();
			if (item.split(" ").length == 2) {
				String[] tmp = item.split(" ");
				if (tmp[1].equals("ASC"))
					sort.append(tmp[0], 1);
				else if (tmp[1].equals("DESC"))
					sort.append(tmp[0], -1);
				else
					throw new RuntimeException("暂不支持的排序条件：" + item);
			} else
				sort.append(str, 1);
		}
		return sort;
	}

	@Override
	public void post() {
		if (this.isBatchSave())
			return;
		Record record = this.getCurrent();
		if (record.getState() == DataSetState.dsInsert) {
			beforePost();
			getDefaultOperator().insert(record);
			super.post();
		} else if (record.getState() == DataSetState.dsEdit) {
			beforePost();
			getDefaultOperator().update(record);
			super.post();
		}
	}

	private IDataOperator getDefaultOperator() {
		if (operator == null) {
			MongoOperator obj = new MongoOperator(this.handle);
			obj.setTableName(SqlOperator.findTableName(this.getCommandText()));
			operator = obj;
		}
		return operator;
	}

	@Override
	public void delete() {
		Record record = this.getCurrent();
		super.delete();
		if (record.getState() == DataSetState.dsInsert)
			return;
		if (this.isBatchSave())
			delList.add(record);
		else {
			getDefaultOperator().delete(record);
		}
	}

	@Override
	public void save() {
		if (!this.isBatchSave())
			throw new RuntimeException("batchSave is false");
		IDataOperator operator = getDefaultOperator();
		// 先执行删除
		for (Record record : delList)
			operator.delete(record);
		delList.clear();
		// 再执行增加、修改
		this.first();
		while (this.fetch()) {
			if (this.getState().equals(DataSetState.dsInsert)) {
				beforePost();
				operator.insert(this.getCurrent());
				super.post();
			} else if (this.getState().equals(DataSetState.dsEdit)) {
				beforePost();
				operator.update(this.getCurrent());
				super.post();
			}
		}
	}

	public IDataOperator getOperator() {
		return operator;
	}

	public void setOperator(IDataOperator operator) {
		this.operator = operator;
	}

	// 将通用类型，转成DataSet，方便操作
	public DataSet getChildDataSet(String field) {
		Object value = this.getField(field);
		if (value == null)
			return null;
		if (!(value instanceof List<?>))
			throw new RuntimeException("错误的数据类型！");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> items = (List<Map<String, Object>>) value;
		DataSet dataSet = new DataSet();
		for (Map<String, Object> item : items) {
			Record record = dataSet.append().getCurrent();
			for (String key : item.keySet())
				record.setField(key, item.get(key));
			record.setState(DataSetState.dsNone);
		}
		return dataSet;
	}

	// 将DataSet转成通用类型，方便存入MongoDB
	public void setChildDataSet(String field, DataSet dataSet) {
		List<Map<String, Object>> items = new ArrayList<>();
		for (Record child : dataSet.getRecords())
			items.add(child.getItems());
		this.setField(field, items);
	}

	@SuppressWarnings("unchecked")
	public List<Object> assignList(String field) {
		Object value = this.getField(field);
		if (value == null) {
			List<Object> items = new ArrayList<>();
			this.setField(field, items);
			return items;
		}
		if (!(value instanceof List<?>))
			throw new RuntimeException("错误的数据类型！");
		return (List<Object>) value;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> assignMap(String field) {
		Object value = this.getField(field);
		if (value == null) {
			Map<String, Object> items = new LinkedHashMap<>();
			this.setField(field, items);
			return items;
		}
		if (!(value instanceof List<?>))
			throw new RuntimeException("错误的数据类型！");
		return (Map<String, Object>) value;
	}
}
