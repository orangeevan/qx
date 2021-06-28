package com.mmorpg.qx.common.socket.core;

/**
 * 属性字段
 * 
 * @author wangke
 * @since v1.0 2016年6月3日
 *
 */
public enum Attribute {
	/**
	 * 玩家登陆认证
	 */
	IDENTITY("IDENTITY"),

	/**
	 * 管理后台认证
	 */
	MANAGEMENT("MANAGEMENT");

	private final String name;

	public boolean hasAttr(Wsession session) {
		return session.getAttributes().containsKey(this.getName());
	}

	private Attribute(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
