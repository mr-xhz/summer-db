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
		push.setMessage("订货单金额为4500元，请您及时接收并确认发货，谢谢！");
		push.setMsgId("16");
		// 发送给指定的设备Id
		// push.send(ClientType.Android, "869159025472386");
		// 发送给指定的设备类型
		push.send(ClientType.Android, null);
		// 发送给所有的用户
		// push.send();
	}

}
