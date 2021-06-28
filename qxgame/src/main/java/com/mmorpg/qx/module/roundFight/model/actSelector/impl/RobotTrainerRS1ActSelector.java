package com.mmorpg.qx.module.roundFight.model.actSelector.impl;

import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActAutoSelector;
import com.mmorpg.qx.module.skill.packet.SkillBuildCdResp;

/**
 * @author wang ke
 * @description: 机器人第一阶段Extract_Card_Before行为
 * @since 20:10 2020-08-18
 */
public class RobotTrainerRS1ActSelector extends AbstractRSActAutoSelector {
    public RobotTrainerRS1ActSelector() {
        super(ObjectType.ROBOT_TRAINER, RoundStage.Extract_Card_Before, AIState.Active);
        orderAIAction(AIState.Active, 1);
        orderAIAction(AIState.Add_Max_Mp, 1, Reason.Round_Add_Mp);
        //orderAIAction(AIState.Use_Magic);
    }

    @Override
    public void ready(RobotTrainerCreature trainer) {
        //每回合初同步技能建筑cd
        // TODO 建筑被取消后，不需要再同步了
        PacketSendUtility.sendPacket(trainer, SkillBuildCdResp.valueOf(trainer));
    }

}
