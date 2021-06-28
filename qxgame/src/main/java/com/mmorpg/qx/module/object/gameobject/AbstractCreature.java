package com.mmorpg.qx.module.object.gameobject;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.object.controllers.AbstractCreatureController;
import com.mmorpg.qx.module.object.controllers.MoveController;
import com.mmorpg.qx.module.object.controllers.ObserveController;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.controllers.stats.CreatureLifeStats;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.object.gameobject.attr.CreatureAttrController;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateTaskManager;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateType;
import com.mmorpg.qx.module.roundFight.packet.UpdatePositionResp;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillController;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生物体,有血有肉的
 *
 * @author wang ke
 * @since v1.0 2018年2月26日
 */
public abstract class AbstractCreature extends AbstractVisibleObject {

    private MoveController moveController;
    private CreatureLifeStats<? extends AbstractCreature> lifeStats;
    private CreatureAttrController attrController;
    private EffectController effectController;
    private ObserveController observeController;

    /**
     * 技能释放完清除
     */
    private SkillController skillController;


    /**
     * 任务状态
     */
    private CreatureUpdateTaskManager creatureUpdateTask = new CreatureUpdateTaskManager(CreatureUpdateType.values());

    /**
     * 死亡后，进入移除状态，进入移除状态后才会被移除
     */
    private volatile boolean removed = false;

    private long deadTime = 0l;

    /**
     * 一个回合内使用魔物娘技能次數
     */
    private int useSkillRS;
    /**
     * 一个回合内使用消耗魔法值
     */
    private int consumeMpRS;

    public AbstractCreature(long objId, AbstractCreatureController<? extends AbstractCreature> controller, WorldPosition position) {
        super(objId, controller, position);
        this.moveController = new MoveController(this);
        this.observeController = new ObserveController();
        this.effectController = new EffectController(this);
        this.attrController = new CreatureAttrController(this);
        this.lifeStats = new CreatureLifeStats<>(this, 0, 0);
        this.skillController = new SkillController(this);
    }

    /**
     * 触发技能公共CD
     */
    private Map<Integer, Long> lastProvokerEffectTimes = new ConcurrentHashMap<Integer, Long>();

    public boolean isProvokeCD(int skillId, long cd) {
        if (!lastProvokerEffectTimes.containsKey(skillId)) {
            return false;
        } else {
            long now = System.currentTimeMillis();
            if ((now - lastProvokerEffectTimes.get(skillId)) < cd) {
                return true;
            }
        }
        return false;
    }

    public void useProvokeSkill(int skillId) {
        lastProvokerEffectTimes.put(skillId, System.currentTimeMillis());
    }

    /**
     * Return CreatureController of this Creature object.
     *
     * @return CreatureController.
     */
    @SuppressWarnings("unchecked")
    @Override
    public AbstractCreatureController<? extends AbstractCreature> getController() {
        return (AbstractCreatureController) super.getController();
    }

    /**
     * @return the moveController
     */
    public MoveController getMoveController() {
        return moveController;
    }

    /**
     * PacketBroadcasterMask
     */
    private volatile int packetBroadcastMask;

    public boolean isCasting() {
        return this.skillController.getCastSkill() != null;
    }


    public int getDispatcherHashCode() {
        return this.hashCode();
    }


    public void setCasting(Skill castingSkill) {
        this.skillController.setCastSkill(castingSkill);
    }

    public AbstractTrainerCreature getMaster() {
        if (this instanceof MWNCreature) {
            return ((MWNCreature) this).getMaster();
        }
        if (this instanceof PlayerTrainerCreature) {
            return (PlayerTrainerCreature) this;
        }
        if (this instanceof RobotTrainerCreature) {
            return (RobotTrainerCreature) this;
        }
        if (this instanceof FireCreature) {
            return ((FireCreature) this).getOwner().getMaster();
        }
        return null;
    }

    public boolean canPerformMove() {
        if (getPosition() != null) {
            return false;
        }
        if (getEffectController().isInStatus(EffectStatus.Stun)) {
            if (this instanceof PlayerTrainerCreature) {
                PacketSendUtility.sendErrorMessage(((PlayerTrainerCreature) this).getOwner(), ManagedErrorCode.PLAYER_STUN);
            }
            return false;
        }
        return true;
    }

    public CreatureLifeStats<? extends AbstractCreature> getLifeStats() {
        return lifeStats;
    }

    public void setLifeStats(CreatureLifeStats<? extends AbstractCreature> lifeStats) {
        this.lifeStats = lifeStats;
    }

    /**
     * 敌我判断
     *
     * @param creature
     * @return
     */
    public boolean isEnemy(AbstractCreature creature) {
        return false;
    }

    public boolean canAttack() {
        return true;
    }

    public EffectController getEffectController() {
        return effectController;
    }

    public void setEffectController(EffectController effectController) {
        this.effectController = effectController;
    }

    public int getPacketBroadcastMask() {
        return packetBroadcastMask;
    }

    public ObserveController getObserveController() {
        return observeController;
    }

    public void setObserveController(ObserveController observeController) {
        this.observeController = observeController;
    }

    public CreatureAttrController getAttrController() {
        return attrController;
    }

    public void setAttrController(CreatureAttrController gameAttrs) {
        this.attrController = gameAttrs;
    }

    public SkillController getSkillController() {
        return skillController;
    }

    public CreatureUpdateTaskManager getCreatureUpdateTask() {
        return creatureUpdateTask;
    }

    public int getCurrentHp() {
        return lifeStats.getCurrentHp();
    }

    public int getCurrentMp(){
        return lifeStats.getCurrentMp();
    }

    public int getHpPercent() {
        return lifeStats.getCurrentHp() / lifeStats.getMaxHp();
    }

    /***
     * 处理触发效果
     * @param triggerType
     */
    public void handleEffect(TriggerType triggerType, AbstractCreature trigger) {
        handleEffect(triggerType, trigger, null);
    }

    /***
     * 处理触发buff，附带参数
     * @param triggerType
     * @param trigger
     * @param param
     */
    public void handleEffect(TriggerType triggerType, AbstractCreature trigger, Object param) {
        this.effectController.triggerEffects(triggerType, trigger, param);
    }

    /**
     * 是否有职业类型属性
     *
     * @return
     */
    public boolean hasJobAttr() {
        for (AttrType job : AttrType.getAllJobType()) {
            if (hasAttr(job)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有元素类型职业
     *
     * @return
     */
    public boolean hasEleAttr() {
        for (AttrType ele : AttrType.getAllEleType()) {
            if (hasAttr(ele)) {
                return true;
            }
        }
        return false;
    }


    public boolean hasAttr(AttrType type) {
        return getAttrController().hasAttr(type);
    }

    /**
     * 发送给客户端当前的位置
     */
    public void sendUpdatePosition() {
        PacketSendUtility.sendPacket(this, UpdatePositionResp.valueOf(this.getObjectId(), getMapId(), this.getGridId()));
    }

    /**
     * 是否活着
     *
     * @return
     */
    public boolean isAlive() {
        return this.lifeStats.isAlive();
    }

    /**
     * 是否已经死亡
     *
     * @return
     */
    public boolean isAlreadyDead() {
        return this.lifeStats.isAlreadyDead();
    }

    /**
     * 是否可被移除
     * @return
     */
    public boolean isRemoved(){
        return removed;
    }

    /**
     * 对象进入移除状态，万事皆休
     */
    public void toRemove(){
        this.removed = true;
    }

    public long getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(long deadTime) {
        this.deadTime = deadTime;
    }

    public int clearUseSkillRS() {
        return useSkillRS = 0;
    }

    public void addUseSkillRS() {
        this.useSkillRS += 1;
    }

    public int getUseSkillRS() {
        return useSkillRS;
    }
    //
    public int clearConsumeMpRS() {
        return consumeMpRS = 0;
    }

    public void addConsumeMpRS(int value) {
        this.consumeMpRS += value;
    }

    public int getConsumeMpRS() {
        return consumeMpRS;
    }
}
