package cn.cerc.jdb.mongo;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import cn.cerc.jdb.core.IDataOperator;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.core.TDateTime;

public class MongoOperator implements IDataOperator {
	private String tableName;
	private MongoSession sess;

	public MongoOperator(IHandle handle) {
		this.sess = (MongoSession) handle.getProperty(MongoSession.sessionId);
	}

	@Override
	public boolean insert(Record record) {
		MongoCollection<Document> coll = sess.getDatabase().getCollection(this.tableName);
		Document doc = Document.parse(getValue(record));
		coll.insertOne(doc);
		return true;
	}

	@Override
	public boolean update(Record record) {
		MongoCollection<Document> coll = sess.getDatabase().getCollection(this.tableName);
		Document doc = Document.parse(getValue(record));
		Object uid = record.getField("_id");
		Object key = uid != null ? new ObjectId(uid.toString()) : "null";
		UpdateResult res = coll.replaceOne(Filters.eq("_id", key), doc);
		return res.getModifiedCount() == 1;
	}

	@Override
	public boolean delete(Record record) {
		MongoCollection<Document> coll = sess.getDatabase().getCollection(this.tableName);
		Object uid = record.getField("_id");
		Object key = uid != null ? new ObjectId(uid.toString()) : "null";
		DeleteResult res = coll.deleteOne(Filters.eq("_id", key));
		return res.getDeletedCount() == 1;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	private String getValue(Record record) {
		Map<String, Object> items = new TreeMap<>();
		for (int i = 0; i < record.getFieldDefs().size(); i++) {
			String field = record.getFieldDefs().getFields().get(i);
			if (field.equals("_id"))
				continue;
			Object obj = record.getField(field);
			if (obj instanceof TDateTime)
				items.put(field, ((TDateTime) obj).toString());
			else if (obj instanceof Date)
				items.put(field, (new TDateTime((Date) obj)).toString());
			else
				items.put(field, obj);
		}
		Gson gson = new GsonBuilder().serializeNulls().create();
		return gson.toJson(items);
	}
}
