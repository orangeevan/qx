package com.mmorpg.qx.module.skill.model.effect;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.condition.AbstractSkillCondition;
import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.condition.StrengthCondConfig;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.manager.EffectManager;
import com.mmorpg.qx.module.skill.model.effect.impl.BlockEffect;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.model.target.TargetManager;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 抽象效果模板
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public abstract class AbstractEffectTemplate {

    private EffectType effectType;

    private int effectResourceId;

    /**
     * 持续回合次数
     */
    protected int duration;

    /**
     * 作用的效果集合
     */
    private Set<AbstractCreature> targets;

    public void init(EffectResource resource) {
        this.effectResourceId = resource.getId();
        this.duration = resource.getDuration();
        this.effectType = resource.getEffectType();
    }

    /**
     * 计算buff效果,效果量化
     *
     * @param effect
     */
    public void calculate(Effect effect) {
        effect.setValue(effect.getEffectResource().getValue());
    }

    public boolean verifyStrengthCond(Conditions strenConditions, Effect effect) {
        //TODO 前期验证
        strenConditions.getConditionList().forEach(condition -> {
            if (AbstractSkillCondition.class.isAssignableFrom(condition.getClass())) {
                throw new RuntimeException("效果加强条件配置错误： " + condition.getType());
            }
        });
        return strenConditions.verify(null, effect, 0) == Result.SUCCESS;
    }

    /**
     * 处理加强
     *
     * @param effect
     */
    public void processStrength(Effect effect) {
        List<StrengthCondConfig> condConfigs = effect.getEffectResource().getAndCreateStrenConditions();
        condConfigs.forEach(config -> {
            Conditions conditions = config.getAndCreateStrenConditions();
            if (verifyStrengthCond(conditions, effect)) {
                effect.setValue((int) (effect.getValue() * config.getStrength()));

            }
            //法强影响
            AbstractCreature effector = effect.getEffector();
            effect.setValue(GameUtil.magicStrAlterEffectValue(effect, effector.getMaster(), effect.getValue()));
        });
    }

    /**
     * 效果生效，持续性效果
     *
     * @param effect
     */
    public abstract boolean applyEffect(Effect effect);

    /**
     * Start effect on effected
     *
     * @param effect
     */
    public void startEffect(Effect effect, EffectController controller) {
        int startRound = 0;
        if (Objects.nonNull(effect.getRoom())) {
            startRound = effect.getRoom().getRound();
        }
        effect.setStartRound(startRound);
        //effect.setEndTime(System.currentTimeMillis() + effect.getEffectResource().getDuration());
        effect.setEndRound(startRound + effect.getEffectResource().getDuration());
    }

    /**
     * 效果替换，目前是相当于添加新效果
     *
     * @param oldEffect
     * @param newEffect
     */
    public void replace(Effect oldEffect, Effect newEffect) {
        if (oldEffect.getEffectResource().isReplace()) {
            oldEffect.setStartRound(newEffect.getStartRound());
            oldEffect.setEndRound(newEffect.getEndRound());
            oldEffect.setLastPeriodRound(0);
        }
    }

    /**
     * 效果结束处理
     *
     * @param effect
     * @param controller
     */
    public void endEffect(Effect effect, EffectController controller) {
        //this.targets = null;
    }

    public int getDuration() {
        return duration;
    }

    public int getEffectResourceId() {
        return effectResourceId;
    }

    public void setEffectResourceId(int effectResourceId) {
        this.effectResourceId = effectResourceId;
    }

    public EffectType getEffectType() {
        return effectType;
    }

    public void setEffectType(EffectType effectType) {
        this.effectType = effectType;
    }

    protected void chooseTargets(Effect effect) {
        EffectResource effectResource = EffectManager.getInstance().getResource(effect.getEffectResourceId());
        if (!CollectionUtils.isEmpty(effectResource.getTargets())) {
            Target target = TargetManager.getInstance().chooseTarget(new ArrayList<>(effectResource.getTargets().keySet()), effect.getEffected());
            if (target != null && effectResource.getMaxTarget() != 0 && !CollectionUtils.isEmpty(target.getTargetIds()) && target.getTargetIds().size() > effectResource.getMaxTarget()) {
                List<Long> subList = target.getTargetIds().subList(0, effectResource.getMaxTarget() - 1);
                target.setTargetIds(subList);
            }
            effect.setEffectTarget(target);
            Set<AbstractCreature> abstractCreatures = RoundFightUtils.getTargetCreatures(target, effect.getEffected().getWorldMapInstance());
            this.targets = abstractCreatures;
        } else if (effect.hasTargets()) {
            Set<AbstractCreature> abstractCreatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
            this.targets = abstractCreatures;
        }
    }

    public Set<AbstractCreature> getTargets() {
        return targets;
    }

    public void setTargets(Set<AbstractCreature> targets) {
        this.targets = targets;
    }

    public Result checkValid(Effect effect) {
        Result result = checkPassiveValid(effect);
        if (result.isFailure()) {
            return result;
        }
        return Result.SUCCESS;
    }

    protected Result checkPassiveValid(Effect effect) {
        AbstractEffectTemplate template = effect.getEffectTemplate();
        if (template instanceof BlockEffect) {
            BlockEffect e = (BlockEffect) template;
            if (e.contain(effect.getEffectResource().getBuffType())) {
                return Result.FAILURE;
            }
        }
        return Result.SUCCESS;
    }
}
