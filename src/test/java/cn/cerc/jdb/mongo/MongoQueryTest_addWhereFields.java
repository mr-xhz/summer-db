package cn.cerc.jdb.mongo;

import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;

import cn.cerc.jdb.core.StubHandle;

public class MongoQueryTest_addWhereFields {
	private StubHandle handle;
	private MongoQuery ds;

	@Before
	public void setUp() throws Exception {
		handle = new StubHandle();
		ds = new MongoQuery(handle);
	}

	@Test
	public void test() {
		String sql = "select * from tmp2 where code='a001' and value=3";
		BasicDBObject filter = new BasicDBObject();
		ds.addWhereFields(filter, sql);
		System.out.println(filter);
	}

}
