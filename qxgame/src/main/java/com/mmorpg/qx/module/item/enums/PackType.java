package com.mmorpg.qx.module.item.enums;

/**
 * 背包类型
 * @author wang ke
 * @since v1.0 2018年3月21日
 *
 */
public enum PackType {

	/** 仓库 */
	WAREHOUSE(1),
	/** 卡组 */
	CARD_BAG(2),
	/** 装备容器 */
	EQUIPMENT_STORAGE(3);

	final private int type;

	private PackType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

}
