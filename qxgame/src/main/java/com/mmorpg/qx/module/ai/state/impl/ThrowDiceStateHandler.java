package com.mmorpg.qx.module.ai.state.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;

/**
 * @author wang ke
 * @description:摇骰子
 * @since 17:06 2020-08-18
 */
public class ThrowDiceStateHandler implements IStateHandler {
    @Override
    public AIState getState() {
        return AIState.Dice;
    }

    @Override
    public void handleState(AbstractTrainerCreature owner, Object... params) {
        RoundFightUtils.throwDice(owner);
    }
}
