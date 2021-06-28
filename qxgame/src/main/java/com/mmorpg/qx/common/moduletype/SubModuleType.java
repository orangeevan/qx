package com.mmorpg.qx.common.moduletype;

/**
 * 
 * @author wang ke
 * @since v1.0 2018年2月28日
 *
 */
public enum SubModuleType {
	/** 商店购买 */
	SHOP_BUY(1),
	/** 任务奖励 */
	QUEST_REWARD(2),
	/** 道具出售 */
	ITEM_SALE(3),
	/** 道具使用 */
	ITEM_USE(4),
	/** 编队解锁 */
	TROOP_UNLOCK(5);

	private final int id;

	SubModuleType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
