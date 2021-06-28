package com.mmorpg.qx.module.object.controllers;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.packet.BuildingInfoResp;
import com.mmorpg.qx.module.object.controllers.packet.NotSeeResp;
import com.mmorpg.qx.module.object.gameobject.*;
import com.mmorpg.qx.module.object.gameobject.building.AbstractBuilding;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.loginposition.AbstractLoginPositionHandle;
import com.mmorpg.qx.module.roundFight.packet.RoomResultResp;
import com.mmorpg.qx.module.roundFight.packet.UpdateRoomStateResp;
import com.mmorpg.qx.module.skill.model.effect.Effect;

import java.util.List;

public class PlayerTrainerController extends AbstractTrainerController<PlayerTrainerCreature> {

    @Override
    public PlayerTrainerCreature getOwner() {
        return (PlayerTrainerCreature) super.getOwner();
    }

    @Override
    public void onSpawn(int mapId, int instanceId) {
        //getOwner().sendUpdatePosition();
        //PacketSendUtility.sendPacket(this.getOwner(), this.getOwner().getInfo());
    }

    @Override
    public void onStopMove() {
        if (getOwner().getLoginPosition() != null) {
            AbstractLoginPositionHandle handle = PlayerManager.getInstance().getLoginPostionHandles()
                    .get(getOwner().getLoginPosition().getType());
            handle.onMove(getOwner());
        }
    }

    @Override
    public void see(AbstractVisibleObject object) {
        super.see(object);
        // 发送一个消息，通知给自己，看到了一个东东（这里需要根据类型来传输不同的对象，减少流量）
        if (object.getObjectType() == ObjectType.PLAYER_TRAINER) {
            PacketSendUtility.sendPacket(getOwner(), ((PlayerTrainerCreature) object).getInfo());
        } else if (object.getObjectType() == ObjectType.MWN) {
            PacketSendUtility.sendPacket(getOwner(), ((MWNCreature) object).getInfo());
        } else if (object.getObjectType() == ObjectType.ROBOT_TRAINER) {
            PacketSendUtility.sendPacket(getOwner(), ((RobotTrainerCreature) object).getRobotTrainerInfo());
        } else if (GameUtil.isInArray(object.getObjectType(), ObjectType.getBuildType())) {
            BuildingInfoResp buildingInfo = ((AbstractBuilding) object).getBuildingInfo();
            PacketSendUtility.sendPacket(getOwner(), buildingInfo);
        }

        // 当玩家新看到一个对象的时候，如果这个对象正在移动，那么就应该把这个对象的移动信息也发送给前端
        //现在移动服务端先行，再同步客户端移动路径
//        if (object instanceof AbstractCreature) {
//            AbstractCreature creature = (AbstractCreature) object;
//            byte[] leftRoads = creature.getMoveController().getLeftRoads();
//            if (leftRoads != null) {
//                PacketSendUtility.sendPacket(getOwner(), MoveResp.valueOf(creature, creature.getPosition().getGridId(), leftRoads));
//            }
//        }
    }

    @Override
    public void notSee(AbstractVisibleObject object, boolean isOutOfRange) {
        super.notSee(object, isOutOfRange);
        PacketSendUtility.sendPacket(getOwner(), NotSeeResp.valueOf(object.getObjectId()));
    }

    /**
     * 驯养师退出房间
     */
    @Override
    public void onExitRoom() {
        /** 同步房间结束状态 */
        PacketSendUtility.sendPacket(getOwner(), UpdateRoomStateResp.valueOf(this.getOwner().getRoom().toRoomVo()));
        /** 同步房间战斗结果 */
        PacketSendUtility.sendPacket(getOwner(), RoomResultResp.valueOf(this.getOwner().getRoom()));
        PlayerTrainerCreature playerTrainerCreature = getOwner();
        if (playerTrainerCreature.getOwner() != null) {
            playerTrainerCreature.getOwner().setTrainerCreature(null);
            playerTrainerCreature.getOwner().setSelectedTrainer(null);
        }
        super.onExitRoom();
    }

    @Override
    public void delete() {
        //驯养师死亡不需要移除
        //super.delete();
    }

    @Override
    public void onBeAttacked(AbstractCreature attacker) {
        super.onBeAttacked(attacker);
        List<Effect> effects = getOwner().getEffectController().getEffects();
        effects.stream().forEach(e -> {
            if (e.getEffectResource().isRemoveBeAttack()) {
                getOwner().getEffectController().removeEffect(e.getEffectResourceId());
            }
        });
    }
}