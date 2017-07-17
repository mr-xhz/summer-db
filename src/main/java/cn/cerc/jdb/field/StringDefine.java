package cn.cerc.jdb.field;

@Deprecated
public class StringDefine extends AbstractDefine {
	private int length;

	public StringDefine() {
		this.setWidth(6);
	}

	public StringDefine(int length) {
		this.length = length;
	}

	@Override
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public boolean validate(Object object) {
		if (object == null)
			return true;
		else if (object.getClass().equals(String.class))
			if (this.getLength() == 0)
				return true;
			else
				return ((String) object).length() <= this.getLength();
		else
			return false;
	}
}
