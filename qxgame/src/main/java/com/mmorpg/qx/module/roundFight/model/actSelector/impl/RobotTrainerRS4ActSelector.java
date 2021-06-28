package com.mmorpg.qx.module.roundFight.model.actSelector.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActAutoSelector;

/**
 * @author wang ke
 * @description:机器人Throw_Dice阶段行为
 * @since 21:08 2020-08-18
 */
public class RobotTrainerRS4ActSelector extends AbstractRSActAutoSelector {
    public RobotTrainerRS4ActSelector() {
        super(ObjectType.ROBOT_TRAINER, RoundStage.Throw_Dice, AIState.Dice);
    }

    @Override
    public void ready(RobotTrainerCreature trainer) {

    }
}
