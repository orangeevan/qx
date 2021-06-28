package com.mmorpg.qx.module.condition;

import com.mmorpg.qx.module.condition.resource.ConditionResource;

public abstract class AbstractCondition<K,V,P> {

	protected String code;
	protected int value;
	private ConditionType type;
	/**条件参数，具体类型具体解析*/
	private String params;

	/***
	 * 验证参数设计时候取参与生物对象AbstractCreature，方便条件组Conditions验证
	 * @param param1 一般情况下为AbstractCreature
	 * @param param2 一般情况下为AbstractCreature
	 * @param amount
	 * @return
	 */
	public abstract Result verify(K param1, V param2, P amount);

	protected AbstractCondition add(AbstractCondition condition) {
		if (check(condition)) {
			return doAdd(condition);
		}
		return condition;
	}

	protected AbstractCondition doAdd(AbstractCondition condition) {
		this.value += condition.value;
		return null;
	}

	/**
	 * 带有resource参数的init方法。方便以后子类扩展新的字段
	 * 
	 * @param resource
	 */
	protected void init(ConditionResource resource) {
		this.value = resource.getValue();
		this.code = resource.getCode();
		this.type = resource.getType();
		this.params = resource.getParams();
		this.init();
	}

	protected void init() {

	}

	protected void calculate(int num) {
		this.value *= num;
	}

	protected boolean check(AbstractCondition condition) {
		if (!condition.getClass().equals(getClass())) {
			return false;
		}
		if (code == null && condition.getCode() != null) {
			return false;
		}

		if (code != null && !code.equals(condition.getCode())) {
			return false;
		}
		return true;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public ConditionType getType() {
		return type;
	}

	public void setType(ConditionType type) {
		this.type = type;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
}
