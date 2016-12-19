package cn.cerc.jdb.field;

import cn.cerc.jdb.core.TDate;

public class TDateDefine extends AbstractDefine {
	public TDateDefine() {
		this.setWidth(5);
	}

	@Override
	public boolean validate(Object object) {
		return object == null || object.getClass().equals(TDate.class);
	}
}
