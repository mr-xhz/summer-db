package cn.cerc.jdb.mongo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
		ds.add("select * from tmp");
		ds.open();
		System.out.println(ds);
	}

	@Test
	public void test_append() {
		ds.add("select * from tmp2");
		ds.open();
		ds.append();
		ds.setField("code", "a001");
		ds.setField("value", 1);
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
	public void test_delete() {
		ds.add("select * from tmp2");
		ds.open();
		while (!ds.eof())
			ds.delete();
	}
}
