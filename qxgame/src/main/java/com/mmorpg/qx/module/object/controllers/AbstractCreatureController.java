package com.mmorpg.qx.module.object.controllers;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.object.controllers.packet.CreatureDieResp;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillReleaseType;
import com.mmorpg.qx.module.skill.model.skillResult.DamageSkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;

import java.util.Collection;
import java.util.Objects;

/**
 * 生物控制器
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public abstract class AbstractCreatureController<T extends AbstractCreature> extends AbstractVisibleObjectController<AbstractCreature> {

    @Override
    public void notSee(AbstractVisibleObject object, boolean isOutOfRange) {
        super.notSee(object, isOutOfRange);
        if (object == getOwner().getTarget()) {
            getOwner().setTarget(null);
        }
    }

    /***
     * 死亡处理
     * @param lastAttacker
     * @param skillId
     */
    @Override
    public void onDie(AbstractCreature lastAttacker, int skillId, int effectId) {
        this.getOwner().setCasting(null);
        /**有些buff死亡触发*/
        if (Objects.nonNull(lastAttacker)) {
            this.getOwner().handleEffect(TriggerType.DIE, lastAttacker);
            broadcastDead(lastAttacker, skillId);
        }
        this.getOwner().getEffectController().dieRemoveAllEffects();
        this.getOwner().getMoveController().stopMoving();
        this.getOwner().setDeadTime(System.currentTimeMillis());
    }

    /***
     * 复活处理
     */
    @Override
    public void onRelive() {
        // PacketSendUtility.broadcastPacketAndReceiver(getOwner(),
        // SM_Relive.valueOf(getOwner(),
        // getOwner().getLifeStats().getCurrentHp(),
        // getOwner().getLifeStats().getCurrentMp()));
    }


    protected void broadcastDead(AbstractCreature lastAttacker, int skillId) {
        PacketSendUtility.broadcastInWorldMap(getOwner(), CreatureDieResp.valueOf(getOwner().getObjectId(), lastAttacker.getObjectId()), true);
    }

    /**
     * 伤害并且广播
     *
     * @param creature
     * @param damage
     */
    public final void attackAndNotifyAttackedObserver(AbstractCreature creature, DamageSkillResult damage) {
        this.onAttack(creature, damage);
        getOwner().getObserveController().notifyAttackedObservers(creature);
    }

    /**
     * 扣血的逻辑实现
     *
     * @param creature
     * @param damage
     */
    public void onAttack(AbstractCreature creature, DamageSkillResult damage) {
//		if (creature.getObjectType() == ObjectType.PLAYER) {
//			// 给玩家的召唤物添加仇恨
//			for (Summon summon : ((PlayerTrainer) creature).getSummons().values()) {
//				summon.getAggroList().addHate(getOwner(), 1);
//			}
//		}
        creature.getLifeStats().reduceHp(damage.getValue(), this.getOwner(), damage.getSkillId(), 0);
        creature.getController().onBeAttacked(getOwner());
    }

    protected void doReward() {

    }

    public void onDialogRequest(Player player) {

    }

    public void onAddDamage() {

    }

    public void onAddHate() {

    }

    public void stopMoving() {
        AbstractCreature owner = getOwner();
        WorldMapService.getInstance().updatePosition(owner, owner.getPosition(), owner.getDir(), true);
        // 通知观察着们自己停止了
        //PacketSendUtility.broadcastInWorldMap(owner.getWorldMapInstance(), MoveResp.valueOf(owner, owner.getPosition().getGridId(), null), null);
    }

    @Override
    public void delete() {
        getOwner().getMoveController().stopMoving();
        getOwner().getEffectController().dieRemoveAllEffects();
        super.delete();
    }

    public void onMove() {
        getOwner().getMoveController().handleMovePass();
        //移动触发效果
        getOwner().handleEffect(TriggerType.MOVE, getOwner());
        getOwner().getObserveController().notifyMoveObservers();
    }

    /**
     * 相遇
     */
    public void onMeet() {
        AbstractCreature ower = getOwner();
        WorldPosition firstPosition = getOwner().getPosition();
        WorldMapInstance mapInstance = WorldMapService.getInstance().getWorldMapInstance(firstPosition.getMapId(), firstPosition.getInstanceId());
        if (mapInstance == null) {
            return;
        }
        MapGrid mapGrid = mapInstance.getParent().getMapGrid(firstPosition.getGridId());
        if (mapGrid == null) {
            return;
        }
        Collection<AbstractVisibleObject> objects = mapInstance.findObjects(mapGrid.getId());
        if (objects.size() == 0) {
            return;
        }

        //和对手相遇
        objects.stream().map(object -> (AbstractCreature) object)
                .filter(object -> RelationshipUtils.judgeRelationship(ower.getMaster(), object.getMaster(), RelationshipUtils.Relationships.ENEMY_TRAINER_TRAINER)).forEach(object -> {
            ower.handleEffect(TriggerType.Move_Pass, ower);
            //ower.handleEffect(TriggerType.LIANX_XI, ower);
        });

    }

    @Override
    public void onSpawn(int mapId, int instanceId) {
        super.onSpawn(mapId, instanceId);
        getOwner().getObserveController().notifySpawnObservers(mapId, instanceId); // 告诉观察者，自己切换地图了
    }

    public void onStopMove() {

    }

    /***
     *  被攻击处理
     * @param attacker
     */
    public void onBeAttacked(AbstractCreature attacker) {

    }

    /**
     * 释放技能
     */
    public void onCastSkill(Skill skill) {
        //触发释放技能效果
        skill.getSkillCaster().handleEffect(TriggerType.SKILL_USE, skill.getSkillCaster());
        //驯养师主动魔法触发伤害添加或减伤
        SkillResource resource = skill.getResource();
        if (resource.getSkillReleaseType() > 0 && resource.getReleaseType() == SkillReleaseType.Trainer_Active_Skill) {
            skill.getSkillCaster().handleEffect(TriggerType.Trainer_Active_SKill, skill.getSkillCaster(), resource.getSkillId());
        }
    }
}
