package com.mmorpg.qx.module.roundFight.model.actSelector.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActAutoSelector;

/**
 * @author wang ke
 * @description:机器人 Move_END 阶段行为
 * @since 09:49 2020-08-19
 */
public class RobotTrainerRS6ActSelector extends AbstractRSActAutoSelector {
    public RobotTrainerRS6ActSelector() {
        super(ObjectType.ROBOT_TRAINER, RoundStage.MOVE_END, AIState.Call_Fight, AIState.Pick_Up_Item);
        orderAIAction(AIState.Use_Magic);
    }

    @Override
    public void ready(RobotTrainerCreature trainer) {
        trainer.setDicePoint(null);
    }

    @Override
    public void end(RobotTrainerCreature trainer) {
        super.end(trainer);
    }
}
