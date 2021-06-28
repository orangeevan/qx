package com.mmorpg.qx.module.object.gameobject.update;

/**
 * 定时更新的任务类型
 * @author wang ke
 * @since v1.0 2018年3月20日
 *
 */
public enum CreatureUpdateType {

	/** 属性更新 */
	ATTR(1),
	/** 当前金钱 */
	PURSE(2),
	/** 经验 */
	EXP(3),
	/** 广播当前生物状态 */
	BROAD_ABNORMALS(4),
	/** 背包存储*/
	ITEM_ENTITY_UPDATE(5),
	/** 玩家PlayerEnt存储*/
	PLAYER_ENTITY_UPDATE(6),
	/** 玩家QuestEntity存储*/
	QUEST_ENTITY_UPDATE(7),
	/** 玩家驯养师数据更新*/
	PLAYER_TRAINER(8),
	/** 玩家魔物娘数据更新*/
	PLAYER_MWN(9),
	/** 玩家公共数据更新*/
	PLAYER_COMMON(10),
	/** 玩家积分 */
	PLAYER_INTEGRAL(11),
	;
	private final int type;

	private CreatureUpdateType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

}
