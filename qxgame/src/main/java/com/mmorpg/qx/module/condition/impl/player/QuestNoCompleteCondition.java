package com.mmorpg.qx.module.condition.impl.player;

import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.module.condition.AbstractCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.player.model.Player;

public class QuestNoCompleteCondition extends AbstractCondition<Player, Object,Integer> {

	@Override
	public Result verify(Player player, Object param2, Integer amount) {
		if (!player.getQuestBox().isCompleted(Integer.valueOf(code), amount)) {
			return Result.SUCCESS;
		}
		return Result.valueOf(ManagedErrorCode.ERROR_MSG);
	}
}
