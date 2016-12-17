package cn.cerc.jdb.field;

public class IntegerDefine extends AbstractDefine {

	public IntegerDefine() {
		this.setWidth(4);
	}

	@Override
	public boolean validate(Object object) {
		return object == null || object.getClass().equals(int.class) || object.getClass().equals(Integer.class);
	}

}
