package com.mmorpg.qx.module.roundFight.model.actSelector.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActAutoSelector;

/**
 * @author wang ke
 * @description:机器人 MOVE 阶段行为
 * @since 09:38 2020-08-19
 */
public class RobotTrainerRS5ActSelector extends AbstractRSActAutoSelector {
    public RobotTrainerRS5ActSelector() {
        super(ObjectType.ROBOT_TRAINER, RoundStage.MOVE, AIState.Move, AIState.Operate_Building);
    }

    @Override
    public void ready(RobotTrainerCreature trainer) {
    }

    @Override
    public void end(RobotTrainerCreature trainer) {
        try {
            trainer.getController().onMoveEnd();
        } finally {
            super.end(trainer);
        }
    }
}
