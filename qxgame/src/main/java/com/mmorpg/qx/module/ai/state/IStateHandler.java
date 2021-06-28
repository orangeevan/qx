package com.mmorpg.qx.module.ai.state;


import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;

/**
 * CZAi状态处理器，根据状态添加Action
 * @author wang ke
 * @since v1.0 2020年7月29日
 *
 */
public interface IStateHandler {
    /**
     * 处理的状态类型
     * @return
     */
    AIState getState();

    /**
     * 处理状态
     * @param owner
     */
    void handleState(AbstractTrainerCreature owner, Object... params);
}
