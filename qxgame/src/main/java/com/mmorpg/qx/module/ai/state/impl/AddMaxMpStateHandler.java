package com.mmorpg.qx.module.ai.state.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;

/**
 * @author wang ke
 * @description:
 * @since 19:29 2020-08-18
 */
public class AddMaxMpStateHandler implements IStateHandler {
    @Override
    public AIState getState() {
        return AIState.Add_Max_Mp;
    }

    @Override
    public void handleState(AbstractTrainerCreature owner, Object... params) {
        //魔法上限加1，恢复魔法
        RoundFightService.getInstance().recoverMpOnNewRound(owner);
    }
}
