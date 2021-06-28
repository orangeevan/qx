package com.mmorpg.qx.module.quest.target;

/**
 * 任务目标类型
 * 
 * @author wang ke
 * @since v1.0 2017年1月3日
 * 
 */
public enum QuestTargetType {

	/** 等级 */
	LEVEL(1),
	/** 杀怪 */
	MONSTER_HUNT(2),
	/** 加入公会 */
	JOIN_GUILD(3),
	/** 公会捐赠 */
	GAIN_DONATE(4),
	/** 进入地图 */
	ENTER_SCENE(5),
	/** 公会宝箱(领取奖励) */
	GUILD_CHEST(6),
	/** 强化装备 */
	ENHANCE_EQUIPMENT(7),
	/** 回收装备 */
	RECYCLE_EQUIPMENT(8),
	/** 搜集装备 */
	GAIN_GOODS(9),
	/** 功勋除魔 */
	KILL_EIVL(10),
	/** 龙魂，星魂 */
	DRAGON_SOUL(11),
	/** 公会宝箱(开启) */
	GUILDTREASURE_OPEN(12),
	/** 材料副本 */
	MATERIAL_MISSION(13),
	/** 历练 */
	EXPERIENCE_ENTER(14),
	/** 神龙 */
	DRAGONTREASURE_EXIT(15),
	/** 悬赏 */
	ARREST(16),
	/** 杀怪 有归属*/
	MONSTER_HUNT_TEAM(17),
	/** 指定场景击杀怪物*/
	FIXED_SCENE_MONSTER_HUNT(18),;

	private int value;

	public QuestTargetType typeOf(int value) {
		for (QuestTargetType type : values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new RuntimeException(String.format("QuestTargetType value[%s] not found!", value));
	}

	private QuestTargetType(int value) {
		this.value = value;
	}

	public QuestTargetHandler getQuestTargetHandler() {
		return QuestTargetHandler.getHandler(this);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
