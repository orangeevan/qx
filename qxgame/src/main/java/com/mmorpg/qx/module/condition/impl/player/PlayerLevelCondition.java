package com.mmorpg.qx.module.condition.impl.player;

import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.module.condition.AbstractCondition;
import com.mmorpg.qx.module.condition.AbstractPlayerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.player.model.Player;

public class PlayerLevelCondition extends AbstractCondition<Player, Object,Integer> {

	private int low;
	private int high;

	@Override
	public Result verify(Player player, Object param2, Integer amount) {
		int level = player.getPlayerEnt().getLevel();
		if (low <= level && level <= high) {
			return Result.SUCCESS;
		}
		return Result.valueOf(ManagedErrorCode.LEVEL);
	}

	public int getLow() {
		return low;
	}

	public void setLow(int low) {
		this.low = low;
	}

	public int getHigh() {
		return high;
	}

	public void setHigh(int high) {
		this.high = high;
	}

}
