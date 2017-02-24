package cn.cerc.jdb.jiguang;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.jdb.core.StubHandle;

public class JiguangPushTest {
	private StubHandle handle;

	@Before
	public void setUp() throws Exception {
		handle = new StubHandle();
	}

	@Test
	public void test() {
		JiguangPush push = new JiguangPush(handle);
		// 消息标题，仅安卓机型有效，IOS设备忽略
		push.setTitle("新在线订单：东江渔具店");
		push.setMessage("【研发测试】向您发送了一条测试消息，如有打扰，请您忽略，谢谢！");
		push.setMsgId("3707");

		// 发送给指定的设备Id
		push.send(ClientType.IOS, "i_FE590CDAAAFD48E3BDB4B6FCC31E1F15");

		push.send(ClientType.Android, "n_862806034034048");

		// 发送给指定的设备类型
		// push.send(ClientType.IOS, null);

		// 发送给所有的用户
		// push.send();
	}

}
