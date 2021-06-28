package com.mmorpg.qx.module.ai.state.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;

/**
 * @author wang ke
 * @description: 激活AI, 每次激活添加魔法跟HP上限
 * @since 11:43 2020-08-17
 */
public class ActiveStateHandler implements IStateHandler {

    @Override
    public AIState getState() {
        return AIState.Active;
    }

    @Override
    public void handleState(AbstractTrainerCreature owner, Object... params) {

    }
}
