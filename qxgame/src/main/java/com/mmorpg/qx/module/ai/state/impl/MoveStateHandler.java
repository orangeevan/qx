package com.mmorpg.qx.module.ai.state.impl;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.object.controllers.move.Road;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.building.AbstractBuilding;
import com.mmorpg.qx.module.roundFight.model.DicePoint;
import com.mmorpg.qx.module.roundFight.packet.TrainerMoveReq;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.worldMap.enums.DirType;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wang ke
 * @description:移动状态处理
 * @since 17:08 2020-08-18
 */
public class MoveStateHandler implements IStateHandler {

    @Override
    public AIState getState() {
        return AIState.Move;
    }

    @Override
    public void handleState(AbstractTrainerCreature owner, Object... params) {
        //检查驯养师是否可移动
        DicePoint dicePoint = owner.getDicePoint();
        if (Objects.isNull(dicePoint) || dicePoint.calcPoints() <= owner.getMoveController().getStep()) {
            return;
        }
        TrainerMoveReq moveReq = new TrainerMoveReq();
        moveReq.setCurrGrid(owner.getGridId());
        moveReq.setDir(owner.getDir());
        moveReq.setStep(dicePoint.calcPoints() - owner.getMoveController().getStep());
        try {
            RoundFightService.getInstance().trainerMove(owner, moveReq);
        }catch (ManagedException e){
            //走常规移动流程可能有业务异常抛出
        }
    }
}
