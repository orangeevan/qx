package com.mmorpg.qx.module.item.enums;

/**
 * 装备位置
 * @author wang ke
 * @since v1.0 2018年3月6日
 *
 */
public enum EquipmentType {
	/** 武器 */
	WEAPON(1),
	/** 衣服 */
	UPPER_BODY(2),
	/** 头盔 */
	HEAD(3),
	/** 鞋子 */
	SHOE(4),
	/** 腰带 */
	WAIST(5),
	/** 项链 */
	NECKLACE(6),
	/** 左戒指 */
	LF_RING(7),
	/** 右戒指 */
	RH_RING(8),
	/** 左手镯 */
	LF_BRACE_LET(9),
	/** 右手镯 */
	RH_BRACE_LET(10),
	/** 护盾 */
	SHIELD(11),
	;

	public static EquipmentType valueOf(int v) {
		for (EquipmentType type : values()) {
			if (type.getIndex() == v) {
				return type;
			}
		}
		throw new RuntimeException(" no match type of EquipmentType[" + v + "]");
	}

	private int index;

	private EquipmentType(int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}

}