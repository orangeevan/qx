package com.mmorpg.qx.module.roundFight.model.actSelector.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActAutoSelector;

/**
 * @author wang ke
 * @description:机器人驯养师 Throw_Dice_Before 行为
 * @since 20:59 2020-08-18
 */
public class RobotTrainerRS3ActSelector extends AbstractRSActAutoSelector {
    public RobotTrainerRS3ActSelector() {
        super(ObjectType.ROBOT_TRAINER, RoundStage.Throw_Dice_Before, AIState.Use_Magic, AIState.Call_Fight);
    }

    @Override
    public void ready(RobotTrainerCreature trainer) {

    }

    @Override
    public void end(RobotTrainerCreature trainer) {
        super.end(trainer);
    }
}
