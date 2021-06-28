package com.mmorpg.qx.module.roundFight.model.actSelector.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActAutoSelector;

/**
 * @author wang ke
 * @description: 机器人 Round_END 结尾阶段
 * @since 09:55 2020-08-19
 */
public class RobotTrainerRS7ActSelector extends AbstractRSActAutoSelector {

    public RobotTrainerRS7ActSelector() {
        super(ObjectType.ROBOT_TRAINER, RoundStage.Round_END, AIState.Idle);
    }

    @Override
    public void ready(RobotTrainerCreature trainer) {

    }
}
