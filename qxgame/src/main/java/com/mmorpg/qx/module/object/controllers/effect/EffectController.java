package com.mmorpg.qx.module.object.controllers.effect;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.controllers.packet.EffectStatusUpdateResp;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.skill.manager.EffectManager;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.SkillReleaseType;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.model.effect.EffectType;
import com.mmorpg.qx.module.skill.packet.EffectAddResp;
import com.mmorpg.qx.module.skill.packet.EffectApplyResp;
import com.mmorpg.qx.module.skill.packet.EffectRemoveResp;
import com.mmorpg.qx.module.skill.packet.EffectTriggerResp;
import com.mmorpg.qx.module.skill.packet.vo.EffectVo;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 技能,buff状态管理器
 * 目前主要buff两大类，一类归属周期性执行，一类归属触发类，如有不能归进这两大类buff，需另外处理
 *
 * @author wang ke
 * @since v1.0 2018年2月26日
 */
public class EffectController {
    private static final Effect[] ZERO_EFFECT_ARRAY = new Effect[]{};
    private Logger logger = SysLoggerFactory.getLogger(EffectController.class);

    private AbstractCreature owner;

    /**
     * 所有技能效果
     */
    protected ConcurrentHashMap<Integer, Effect> effects = new ConcurrentHashMap<>();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 当前效果状态,因为可能有多个effect产生同一个效果.计数器为0的时候效果才消失
     */
    //protected ConcurrentHashMap<EffectStatus, AtomicLong> effectStatus = new ConcurrentHashMap<>();

    /**
     * 效果状态集合
     */
    private volatile BitSet status = new BitSet();

    private final Object lock = new Object();

    public EffectController(AbstractCreature owner) {
        this.owner = owner;
    }

    /**
     * @return the owner
     */
    public AbstractCreature getOwner() {
        return owner;
    }

    /**
     * 定时周期性buff执行效果逻辑
     */
    public void schedule() {
        synchronized (lock) {
            for (Effect effect : effects.values()) {
                try {
                    //先检查回合到期，到期移除
                    if (isEndRound(effect)) {
                        removeEffect(effect.getEffectResourceId(), true);
                        continue;
                    }
                    //触发类效果不走心跳机制
                    if (effect.isTriggerType()) {
                        continue;
                    }
                    if (effect.onAddRS() && effect.isDelayType() && effect.onDelayEffectRound()) {
                        applyEffect(effect);
                        continue;
                    }
                    if (effect.isPeriodicEffect()) {
                        applyPeriodEffect(effect);
                        continue;
                    }

                } catch (Exception e) {
                    logger.error("", e);
                } finally {
                    continue;
                }
            }
        }
    }

    /***
     * 尝试效果生效
     * @param effect
     */
    private boolean applyEffect(Effect effect) {
        //触发条件判断
        //Result verify = effect.getEffectResource().getAndCreateTriggerConditions().verify(null, effect, 0);
        if (effect.isTriggerType()) {
            return false;
        }
        if (effect.isDelayType() && !effect.onDelayEffectRound()) {
            return false;
        }
        Result result = effect.getEffectTemplate().checkValid(effect);
        if (result.isFailure()) {
            return false;
        }
        if (effect.applyEffect()) {
            effect.addUseTime();
            effect.setPeriodRound(effect.getRoom().getRound() + effect.getEffectResource().getPeriod());
            notifyEffectApply(effect);
            getOwner().handleEffect(TriggerType.Effect_Trigger, owner, effect);
            return true;
        }
        checkAndRemove(effect);
        return false;
    }

    /***
     * 效果生效,定时执行间隔性buff
     * @param effect
     */
    private void applyPeriodEffect(Effect effect) {
        //触发条件判断
        //Result verify = effect.getEffectResource().getAndCreateConditions().verify(null, effect, 0);
        if (!effect.isPeriodicEffect()) {
            return;
        }
        //周期性buff不需要再检查触发条件
        //if (verify.isFailure()) {
        //    return;
        //}
        if (effect.onAddRS() && effect.onPeriodRound()) {
            effect.periodicAction();
            getOwner().handleEffect(TriggerType.Effect_Trigger, owner, effect);
        }

        checkAndRemove(effect);
    }

    /***
     * 触发效果,没有触发类型buff处理
     * @param effect
     */
//    private void applyEffectNoTrigger(Effect effect) {
//
//        //触发条件判断
//        List<AbstractSkillCondition> conditionType = effect.getEffectResource().getAndCreateConditions().findConditionType(AbstractSkillCondition.class);
//        for (AbstractSkillCondition condition : conditionType) {
//            Result verify = condition.verify(null, effect, 0);
//            if (verify.isFailure()) {
//                return;
//            }
//        }
//
//        if (effect.applyEffect()) {
//            effect.addUseTime();
//        }
//        if (effect.getElapsedTime() <= 0) {
//            effect.endEffect();
//        } else if (effect.getEffectResource().isAfterUseRemove()) {
//            effect.endEffect();
//        } else if (effect.isUsedOver()) {
//            effect.endEffect();
//        }
//    }

    /**
     * @param effect
     */
    public void addEffect(Effect effect) {
        AbstractCreature effector = effect.getEffector();
        AbstractCreature effected = effect.getEffected();
        int skillResourceId = effect.getSkillResourceId();
        if (skillResourceId > 0) {
            //是否免疫该技能效果
            if (effected.getEffectController().getEffect(EffectType.Miss) != null) {
                SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillResourceId);
                SkillReleaseType releaseType = skillResource.getReleaseType();
                if (releaseType == SkillReleaseType.Trainer_Active_Skill ||
                        releaseType == SkillReleaseType.Trainer_Genius_Skill ||
                        releaseType == SkillReleaseType.Mwn_Job_Skill ||
                        releaseType == SkillReleaseType.Boss_Skill) {
                    return;
                }

            }
        }

        synchronized (lock) {
            if (!addEffectsOnly(effect)) {
                return;
            }
            notifyAddEffect(effect);
            String info = String.format("效果添加，效果释放者：【 %s】 效果承受着：【%s】, 效果id：【%d】, 效果添加阶段：【%s】,当前回合数：【%s】, 效果结束回合：【%s】",
                    effect.getEffector().getObjectId(), effect.getEffected().getObjectId(), effect.getEffectResourceId(), effect.getAddRS(), effect.getRoom().getRound(), effect.getEndRound());
            System.err.println(info);
            logger.info(info);
            //过度操作，所有effect目前都在心跳处理，无需在此处多次重复处理
            //schedule();
            //添加时候尝试处理
            applyEffect(effect);
        }
    }

    public boolean addEffectsOnly(Effect effect) {
        Result result = checkAddEffect(effect);
        if (result.isFailure()) {
            return false;
        }
        Effect oldEffect = effects.get(effect.getEffectResourceId());
        if (oldEffect != null) {
            oldEffect.replace(effect);
        } else {
            effects.put(effect.getEffectResourceId(), effect);
            effect.startEffect(this);
        }
        return true;
    }


    private Result checkAddEffect(Effect effect) {
        AbstractCreature effector = effect.getEffector();
        AbstractCreature effected = effect.getEffected();
        int skillResourceId = effect.getSkillResourceId();
        if (skillResourceId > 0) {
            //是否免疫该技能效果
            if (effected.getEffectController().getEffect(EffectType.Miss) != null) {
                SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillResourceId);
                SkillReleaseType releaseType = skillResource.getReleaseType();
                if (releaseType == SkillReleaseType.Trainer_Active_Skill ||
                        releaseType == SkillReleaseType.Trainer_Genius_Skill ||
                        releaseType == SkillReleaseType.Mwn_Job_Skill ||
                        releaseType == SkillReleaseType.Boss_Skill) {
                    return Result.FAILURE;
                }

            }
        }
        Effect[] es = effects.values().toArray(ZERO_EFFECT_ARRAY);
        for (Effect e : es) {
            if (e.getEffectResource().rejectEffect(effect.getType())) {
                return Result.FAILURE;
            }
        }
        return Result.SUCCESS;
    }

    public void removeAllEffects() {
        synchronized (lock) {
            if (CollectionUtils.isEmpty(effects)) {
                return;
            }
            Iterator<Effect> iterator = effects.values().iterator();
            while (iterator.hasNext()) {
                removeEffect(iterator.next().getEffectResourceId());
            }
        }
    }

    public void removeEffects(int buffType) {
        synchronized (lock) {
            if (CollectionUtils.isEmpty(effects)) {
                return;
            }
            Iterator<Effect> iterator = effects.values().iterator();
            while (iterator.hasNext()) {
                Effect effect = iterator.next();
                EffectResource resource = EffectManager.getInstance().getResource(effect.getEffectResourceId());
                if (resource.getBuffType() == buffType) {
                    removeEffect(effect.getEffectResourceId());
                }
            }
        }
    }

    /**
     * @param effectId
     * @return abnormalEffectMap
     */
    public Effect getEffect(int effectId) {
        return effects.get(effectId);
    }

    public boolean contains(int effectId) {
        return effects.containsKey(effectId);
    }

    /***
     * 广播buff效果状态
     */
    public void broadcastStatus(EffectStatus effectStatus, boolean gain) {
        EffectStatusUpdateResp resp = EffectStatusUpdateResp.value(effectStatus, gain);
        PacketSendUtility.broadcastInWorldMap(getOwner(), resp, true);
    }

    /***
     * 移除效果，同步前端
     * @param effectId
     */
    public void removeEffect(int effectId) {
        removeEffect(effectId, true);
    }

    /**
     * 移除效果，添加同步标志
     *
     * @param effectId
     * @param needSync
     */
    private void removeEffect(int effectId, boolean needSync) {
        Effect effect = effects.remove(effectId);
        if (Objects.isNull(effect)) {
            return;
        }
        boolean broadcast;
        synchronized (lock) {
            String info = String.format("效果移除，效果释放者：【 %s】 效果承受着：【%s】, 效果id：【%d】, 效果添加阶段：【%s】, 房间实际阶段：【%s】,当前回合数：【%s】",
                    effect.getEffector().getObjectId(), effect.getEffected().getObjectId(), effect.getEffectResourceId(), effect.getAddRS(), effect.getRoom().getRoundStage(), effect.getRoom().getRound());
            System.err.println(info);
            logger.info(info);
            effect.endEffect();
            broadcast = effect.getEffectResource().isAddBroadcast();
        }
        if (!needSync) {
            return;
        }
        EffectRemoveResp effectRemoveResp = new EffectRemoveResp();
        effectRemoveResp.setObjectId(getOwner().getObjectId());
        effectRemoveResp.setEffectId(effectId);
        if (broadcast) {
            PacketSendUtility.broadcastInWorldMap(getOwner(), effectRemoveResp, true);
        } else {
            PacketSendUtility.sendPacket(getOwner(), effectRemoveResp);
        }
    }

    /**
     * Removes the effect by skillid.
     *
     * @param skillid
     */
    public void removeSkillEffects(int skillid) {
        synchronized (lock) {
            for (Effect effect : effects.values()) {
                if (effect.getSkillResourceId() == skillid) {
                    removeEffect(effect.getEffectResourceId());
                }
            }
        }
    }

    /***
     * 检查是否带有技能效果
     * @param skillId
     * @return
     */
    public boolean containsSkillEffect(int skillId) {
        return effects.values().stream().anyMatch(effect -> effect.getSkillResourceId() == skillId);
    }

    /**
     * Removes all effects from controllers and ends them appropriately Passive
     * effect will not be removed
     */
    public void dieRemoveAllEffects() {
        if (CollectionUtils.isEmpty(effects)) {
            return;
        }
        synchronized (lock) {
            Iterator<Effect> iterator = effects.values().iterator();
            while (iterator.hasNext()) {
                Effect effect = iterator.next();
                if (effect.isDeadRemove()) {
                    removeEffect(effect.getEffectResourceId(), false);
                }
            }
        }
    }

    public void setStatus(EffectStatus status, boolean broadcast) {
        synchronized (lock) {
//            if (!effectStatus.containsKey(status)) {
//                effectStatus.put(status, new AtomicLong(0));
//            }
//            if (effectStatus.get(status).incrementAndGet() == 1) {
//                this.status |= status.getId();
//                if (broadcast) {
//                    broadcastStatus();
//                }
//            }
            //this.status |= status.getId();
            this.status.set(status.getId());
            if (broadcast) {
                broadcastStatus(status, true);
            }
        }
    }

    public void unsetStatus(EffectStatus status, boolean broadcast) {
        synchronized (lock) {
//            if (effectStatus.containsKey(status) && effectStatus.get(status).decrementAndGet() == 0) {
//                this.status &= ~status.getId();
//                if (broadcast) {
//                    broadcastStatus();
//                }
//            }
            //this.status &= ~status.getId();
            this.status.clear(status.getId());
            if (broadcast) {
                broadcastStatus(status, false);
            }
        }
    }

    public boolean isInStatus(EffectStatus status) {
        synchronized (lock) {
            //return (this.status & status.getId()) == status.getId();
            return !this.status.isEmpty() && this.status.get(status.getId());
        }
    }

    /***
     * 获取指定类型效果，如果有多个拿第一个
     * @param type
     * @return
     */
    public Effect getEffect(EffectType type) {
        Optional<Effect> result = effects.values().stream().filter(effect -> effect.getType() == type).findFirst();
        return result.orElse(null);
    }

    /***
     * 获取指定类型的所有效果
     * @param type
     * @return
     */
    public Collection<Effect> getEffects(EffectType type) {
        return effects.values().stream().filter(effect -> effect.getType() == type).collect(Collectors.toList());
    }

    /***
     * 根据触发类型，触发对应魔法效果
     * @param triggerType
     */
    public void triggerEffects(TriggerType triggerType, AbstractCreature trigger, Object param) {
        if (CollectionUtils.isEmpty(effects)) {
            return;
        }
        synchronized (lock) {
            effects.values().stream().filter(this::judgeTurn).filter(effect -> canEffectTrigger(effect, triggerType, trigger, param)).forEach(effect -> {
                try {
                    Result result = effect.getEffectTemplate().checkValid(effect);
                    if (result.isFailure()) {
                        return;
                    }
                    effect.setTrigger(trigger);
                    if (effect.applyEffect()) {
                        System.err.println(String.format("效果触发生效 触发类型【 %s】，效果释放者：【 %s】 效果承受着：【%s】, 效果id：【%d】", triggerType, effect.getEffector().getObjectId(), effect.getEffected().getObjectId(), effect.getEffectResourceId()));
                        logger.info(String.format("效果触发生效 触发类型【 %s】，效果释放者：【 %s】 效果承受着：【%s】, 效果id：【%d】", triggerType, effect.getEffector().getObjectId(), effect.getEffected().getObjectId(), effect.getEffectResourceId()));
                        effect.addUseTime();
                        effect.setPeriodRound(effect.getRoom().getRound() + effect.getEffectResource().getPeriod());
                        notifyEffectApply(effect);
                        notifyTriggerEffect(effect);
                        trigger.handleEffect(TriggerType.Effect_Trigger, trigger, effect);
                    }
                    checkAndRemove(effect);
                } catch (Exception e) {
                    logger.error("", e);
                }
            });
        }
    }

    /**
     * 是否有效果被触发
     *
     * @param triggerType
     * @param trigger
     * @param param
     * @return
     */
    public boolean hasAnyEffectTrigger(TriggerType triggerType, AbstractCreature trigger, Object param) {
        if (CollectionUtils.isEmpty(effects)) {
            return false;
        }
        for (Effect effect : effects.values()) {
            try {
                if (canEffectTrigger(effect, triggerType, trigger, param)) {
                    return true;
                }
            } catch (Exception e) {
                logger.error("效果触发检查异常", e);
            } finally {
                continue;
            }
        }
        return false;
    }

    public boolean canEffectTrigger(Effect effect, TriggerType triggerType, AbstractCreature trigger, Object param) {
        if (StringUtils.isEmpty(effect.getEffectResource().getTriggerCondition())) {
            return false;
        }
        if (effect.isDelayType() && !effect.onDelayEffectRound()) {
            return false;
        }
        if (effect.getRoom().getRound() < effect.getPeriodRound()) {
            return false;
        }
        Conditions conditions = effect.getEffectResource().getAndCreateTriggerConditions();
        //目前触发条件满足其一就可以触发
        List<AbstractEffectTriggerCondition> triggerConditions = conditions.findConditionType(AbstractEffectTriggerCondition.class);
        for (AbstractEffectTriggerCondition condition : triggerConditions) {
            if (condition.getTriggerType() != triggerType) {
                continue;
            }
            Result verify = null;
            // 具体效果类型验证参数不一样，统一处理
            verify = condition.verify(trigger, triggerType, param);

            if (Objects.nonNull(verify) && verify.isSuccess()) {
                return true;
            }
        }
        return false;
    }

    public void handleEffectorAfterDie(AbstractCreature die) {
        if (CollectionUtils.isEmpty(effects)) {
            return;
        }
        List<Effect> effectors = effects.values().stream().filter(effect -> effect.getEffector() == die).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(effectors)) {
            return;
        }
        effectors.stream().forEach(effect -> {
            this.removeEffect(effect.getEffectResourceId());
        });
    }

    public List<EffectVo> toEffectVo() {
        if (CollectionUtils.isEmpty(effects)) {
            return new ArrayList<>();
        }
        synchronized (lock) {
            List<EffectVo> effectVos = effects.values().stream().map(EffectVo::valueOf).collect(Collectors.toList());
            return effectVos;
        }
    }

    public void notifyAddEffect(Effect effect) {
        EffectAddResp addResp = EffectAddResp.valueOf(effect);
        if (effect.getEffectResource().isAddBroadcast()) {
            PacketSendUtility.broadcastInWorldMap(this.owner, addResp, true);
        } else {
            PacketSendUtility.sendPacket(this.owner, addResp);
        }
    }

    public void notifyTriggerEffect(Effect effect) {
        EffectTriggerResp triggerResp = EffectTriggerResp.valueOf(effect);
        if (effect.getEffectResource().isTriggerbroadcast()) {
            PacketSendUtility.broadcastInWorldMap(this.owner, triggerResp, true);
        } else {
            PacketSendUtility.sendPacket(this.owner, triggerResp);
        }
    }

    /**
     * 效果生效
     *
     * @param effect
     */
    public void notifyEffectApply(Effect effect) {
        EffectApplyResp applyResp = EffectApplyResp.valueOf(effect);
        if (effect.getEffectResource().isAddBroadcast()) {
            PacketSendUtility.broadcastInWorldMap(this.owner, applyResp, true);
        } else {
            PacketSendUtility.sendPacket(this.owner, applyResp);
        }
    }


    /**
     * 获取具体类型效果
     *
     * @param buffType
     * @return
     */
    public List<Effect> getEffectsByBuffType(final int buffType) {
        if (CollectionUtils.isEmpty(effects)) {
            return null;
        }
        synchronized (lock) {
            return effects.values().stream().filter(effect -> effect.getEffectResource().getBuffType() == buffType).collect(Collectors.toList());
        }
    }

    /**
     * 没有任何效果
     *
     * @return
     */
    public boolean hasNoEffects() {
        return effects.isEmpty();
    }

    /**
     * 效果是否结束
     *
     * @param effect
     * @return
     */
    public boolean isEndRound(Effect effect) {
        if (!judgeTurn(effect)) {
            return false;
        }
        //效果指定阶段移除
        if (effect.isFixRSRemove()) {
            //判断是否满足指定回合阶段
            if (effect.getEffectResource().getRemoveStage() != getOwner().getMaster().getRoom().getRoundStage()) {
                return false;
            }
        } else {
            if (!effect.onAddRS()) {
                return false;
            }
        }
        return effect.isEndRound();
    }

    public boolean judgeTurn(Effect effect) {
        //判断敌我回合
        AbstractTrainerCreature master = getOwner().getMaster();
        if (effect.getEffectResource().limitSelfTurn() && master.getRoom().getCurrentTurn() != master) {
            return false;
        }
        if (effect.getEffectResource().limitEnemyTurn() && master.getRoom().getCurrentTurn() == master) {
            return false;
        }
        return true;
    }

    /**
     * 检查并移除effect
     *
     * @param effect
     */
    private void checkAndRemove(Effect effect) {
        if (isEndRound(effect)) {
            removeEffect(effect.getEffectResourceId());
        } else if (effect.getEffectResource().isAfterUseRemove()) {
            removeEffect(effect.getEffectResourceId());
        } else if (effect.isUsedOver()) {
            removeEffect(effect.getEffectResourceId());
        }
    }

    public List<Effect> getEffects() {
        return new ArrayList<>(effects.values());
    }
}
