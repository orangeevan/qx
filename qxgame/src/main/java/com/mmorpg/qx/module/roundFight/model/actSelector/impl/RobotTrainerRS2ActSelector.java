package com.mmorpg.qx.module.roundFight.model.actSelector.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActAutoSelector;

/**
 * @author wang ke
 * @description:机器人第二阶段行为
 * @since 20:37 2020-08-18
 */
public class RobotTrainerRS2ActSelector extends AbstractRSActAutoSelector {
    public RobotTrainerRS2ActSelector() {
        super(ObjectType.ROBOT_TRAINER, RoundStage.Extract_Card, AIState.Extract_Card);
        orderAIAction(AIState.Extract_Card, 1);
    }

    @Override
    public void ready(RobotTrainerCreature trainer) {

    }
}
