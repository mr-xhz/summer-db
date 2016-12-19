package cn.cerc.jdb.field;

public abstract class AbstractDefine {
	private String code;
	private String name;
	private int width = 0;
	// 是否为计算字段，若为true则不予保存到数据库中
	private boolean calculated;

	// 设置字段代码
	public AbstractDefine setCode(String code) {
		this.code = code;
		return this;
	}

	// 返回字段代码
	public String getCode() {
		return code;
	}

	// 返回字段名称
	public String getName() {
		return name != null ? name : code;
	}

	// 设置字段名称
	public AbstractDefine setName(String name) {
		this.name = name;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public String toString() {
		return String.format("code:%s, name:%s, length:%s, precision:%d, scale:%d", getCode(), getName(), getLength(),
				getPrecision(), getScale());
	}

	// 是否为计算字段：若为计算字段，则不会写入到后台数据表
	public boolean isCalculated() {
		return calculated;
	}

	public AbstractDefine setCalculated(boolean calculated) {
		this.calculated = calculated;
		return this;
	}

	// 默认为0，表示长度不管控
	public int getLength() {
		return 0;
	}

	// 数值长度，仅适用于数值型
	public int getPrecision() {
		return 0;
	}

	// 小数位数，仅适用于数值型
	public int getScale() {
		return 0;
	}

	// 判断传入值是否为该字段许可的类型
	public abstract boolean validate(Object object);

}
