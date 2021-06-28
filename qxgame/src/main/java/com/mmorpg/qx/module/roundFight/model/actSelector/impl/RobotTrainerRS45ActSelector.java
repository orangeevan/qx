package com.mmorpg.qx.module.roundFight.model.actSelector.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActAutoSelector;

/**
 * @author wang ke
 * @description: 投完骰子后阶段，移动前
 * @since 19:08 2020-11-13
 */
public class RobotTrainerRS45ActSelector extends AbstractRSActAutoSelector {
    public RobotTrainerRS45ActSelector(){
        super(ObjectType.ROBOT_TRAINER, RoundStage.Throw_Dice_After, AIState.Call_Fight);
        orderAIAction(AIState.Call_Fight);
    }

    @Override
    public void ready(RobotTrainerCreature trainer) {

    }
}
