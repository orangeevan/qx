package com.mmorpg.qx.module.player.model;

public enum LoginPositionType {
	/** 默认登陆器 */
	DEFAULT(0),
	/** 普通的世界场景 */
	WORLD_SCENE(1);

	private int value;

	private LoginPositionType(int value) {
		this.value = value;
	}

	public static LoginPositionType valueOf(int value) {
		for (LoginPositionType type : values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		return null;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
