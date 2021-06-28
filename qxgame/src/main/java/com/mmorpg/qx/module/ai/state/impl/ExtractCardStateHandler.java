package com.mmorpg.qx.module.ai.state.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;

/**
 * @author wang ke
 * @description: 抽卡行为
 * @since 16:34 2020-08-18
 */
public class ExtractCardStateHandler implements IStateHandler {
    @Override
    public AIState getState() {
        return AIState.Extract_Card;
    }

    @Override
    public void handleState(AbstractTrainerCreature owner, Object... params) {
        RoundFightService.getInstance().roundAddCard(owner, 1, Reason.Round_Extract_Card);
    }
}
