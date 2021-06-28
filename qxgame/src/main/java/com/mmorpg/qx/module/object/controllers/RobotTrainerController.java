package com.mmorpg.qx.module.object.controllers;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.skill.model.effect.Effect;

import java.util.List;

/**
 * @author wang ke
 * @description:
 * @since 15:17 2020-08-17
 */
public class RobotTrainerController extends AbstractTrainerController<RobotTrainerCreature> {
    @Override
    public RobotTrainerCreature getOwner() {
        return (RobotTrainerCreature) super.getOwner();
    }

    /**
     * 机器人驯养师出生地图，全地图广播
     *
     * @param mapId
     * @param instanceId
     */
    @Override
    public void onSpawn(int mapId, int instanceId) {
        //PacketSendUtility.broadcastInWorldMap(this.getOwner(), this.getOwner().getRobotTrainerInfo(), true);
    }

    @Override
    public void onStopMove() {
        //停止移动交给MoveController处理
    }

    @Override
    public void onBeAttacked(AbstractCreature attacker) {
        super.onBeAttacked(attacker);
        super.onBeAttacked(attacker);
        List<Effect> effects = getOwner().getEffectController().getEffects();
        effects.stream().forEach(e -> {
            if (e.getEffectResource().isRemoveBeAttack()) {
                getOwner().getEffectController().removeEffect(e.getEffectResourceId());
            }
        });
    }
}
