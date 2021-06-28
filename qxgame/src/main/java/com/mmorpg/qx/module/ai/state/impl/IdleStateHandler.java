package com.mmorpg.qx.module.ai.state.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;

/**
 * @author wang ke
 * @description: 空闲状态处理
 * @since v1.0 11:56 2020-07-30
 */
public class IdleStateHandler implements IStateHandler {
    @Override
    public AIState getState() {
        return AIState.Idle;
    }

    @Override
    public void handleState(AbstractTrainerCreature owner, Object... params) {
        //nothing
    }

}
