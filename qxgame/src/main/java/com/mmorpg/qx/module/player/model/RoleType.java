package com.mmorpg.qx.module.player.model;

/**
 * 角色类型
 */
public enum RoleType {

	ZHAN_SHI(1),

	FA_SHI(2),

	DAO_SHI(3);

	private int id;

	private RoleType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static RoleType getById(int id) {
		for (RoleType type : RoleType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		throw new RuntimeException("role not found " + id);
	}

}
