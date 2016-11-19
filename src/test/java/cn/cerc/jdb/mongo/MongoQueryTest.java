package cn.cerc.jdb.mongo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.core.DataSet;
import cn.cerc.jdb.core.StubHandle;

public class MongoQueryTest {
	private StubHandle handle;
	private MongoQuery ds;

	@Before
	public void setUp() throws Exception {
		handle = new StubHandle();
		ds = new MongoQuery(handle);
	}

	@Test
	@Ignore
	public void test_open() {
		ds.add("select * from tmp2");
		ds.open();
		System.out.println(ds);
		while (ds.fetch()) {
			// 输出子数据表
			DataSet child = ds.getChildDataSet("data");
			System.out.println(child);
		}
	}

	@Test
	@Ignore
	public void test_append() {
		DataSet data = new DataSet();
		data.append();
		data.setField("it", 1);
		data.setField("month", "201609");
		data.append();
		data.setField("it", 2);
		data.setField("month", "201610");
		//
		ds.add("select * from tmp2 where code='a001'");
		ds.open();
		ds.append();
		ds.setField("code", "a001");
		ds.setField("value", 1);
		ds.setChildDataSet("data", data);
		DataSet ds2 = ds.getChildDataSet("data");
		System.out.println(ds2);
		ds.post();
	}

	@Test
	public void test_modify() {
		ds.add("select * from tmp2");
		ds.open();
		while (ds.fetch()) {
			ds.edit();
			ds.setField("value", ds.getInt("value") + 1);
			ds.post();
		}
	}

	@Test
	@Ignore
	public void test_delete() {
		ds.add("select * from tmp2 where code='a001' and value=3");
		ds.open();
		System.out.println(ds);
		while (!ds.eof())
			ds.delete();
	}
}
