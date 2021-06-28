package com.mmorpg.qx.common.rule;

import com.mmorpg.qx.module.player.model.Player;

/**
 * 在玩家身上的实体对象存储的规则接口
 * @author wang ke
 * @since v1.0 2018年3月21日
 *
 */
public interface EntityOfPlayerUpdateRule {
	/**
	 * 玩家初始化时调用的接口.用于数据库对象的insert与领域对象的创建.并且关联到Player对象上去
	 * @param player
	 */
	void initPlayer(Player player);

	/**
	 * 当数据变更时,需要调用的存储.可以有延迟不一定即时
	 * @param player
	 */
	void update(Player player);

	/**
	 * 为了保险起见.建议在玩家登出时即时存储一次.如果update(Player player)
	 * @param player
	 */
	void logoutWriteBack(Player player);
}
