package com.mmorpg.qx.module.skill.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;
import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.condition.ConditionManager;
import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.condition.StrengthCondConfig;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.skill.model.effect.EffectType;
import com.mmorpg.qx.module.skill.model.target.TargetType;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 技能效果配置表
 *
 * @author wang ke
 * @since v1.0 2018年3月1日
 */
@Resource
public class EffectResource {
    @Id
    private int id;
    /**
     * 效果类型
     */
    private EffectType effectType;
    /**
     * 基础数值
     */
    private int value;
    /**
     * 效果持续回合数
     */
    private int duration;
    /**
     * 周期性,回合
     */
    private int period;

    /**
     * 增减
     */
    private boolean add;
    /**
     * 最大叠加时间
     */
    private int maxOverlyTime;
    /**
     * 最大叠加层数
     */
    private int maxOverlyCount = 1;
    /**
     * 延迟生效
     */
    private int delayRound;

    /**
     * 是否死亡移除
     */
    private boolean deadRemove;
    /**
     * 释放完成以后移除的effectId效果
     */
    private boolean afterUseRemove;
    /**
     * 是否广播给周围的玩家
     */
    private boolean broadcast;
    /**
     * 是否替换
     */
    private boolean replace;
    /**
     * 触发条件
     */
    private String triggerCondition;

    /**
     * 使用条件
     */
    private transient Conditions triggerConditions;
    /**
     * 使用主动技能后移除
     */
    private boolean removeUseSkill;
    /**
     * 受攻击后移除
     */
    private boolean removeBeAttack;

    /**
     * 最大次数
     */
    private int maxTimes;

    /**
     * 参数
     */
    private String param;

    /**
     * 触发广播
     */
    private boolean triggerbroadcast;

    /**
     * 目标类型
     */
    private String targetType;
    private Map<TargetType, String> targets;

    /**
     * 法强影响 0不受 1受影响
     */
    private int abilityPower;

    private String strengthCondition;

    private transient List<StrengthCondConfig> strenConditions;
//
//    private int strength;


    /**
     * 1：万分比比例系数
     * 2：纯数值变化
     */
    private int strengthValueType;

    /**
     * 定义buff类型，方便后续做逻辑操作
     */
    private int buffType;

    /**
     * 值类型
     */
    private int valueType;

    private int maxTarget;

    /**
     * 回合数结束阶段
     */
    private RoundStage removestage;

    /**
     * 己方：0
     * 敌方：1
     * 不分敌方：2
     */
    private int camp;

    /**
     * 不能共存的效果类型
     */
    private String rejectEffectStr;
    private Set<EffectType> rejectEffects;

    public Conditions getAndCreateTriggerConditions() {
        if (triggerConditions == null) {
            if (StringUtils.isEmpty(triggerCondition)) {
                triggerConditions = new Conditions();
            } else {
                List<ConditionResource> conditionResources = JsonUtils.string2List(triggerCondition, ConditionResource.class);
                triggerConditions = ConditionManager.createConditions(conditionResources);
            }
        }
        return triggerConditions;
    }

    public List<StrengthCondConfig> getAndCreateStrenConditions() {
        if (strenConditions == null) {
            if (StringUtils.isEmpty(strengthCondition)) {
                strenConditions = Collections.EMPTY_LIST;
            } else {
                List<StrengthCondConfig> conditionResources = JsonUtils.string2List(strengthCondition, StrengthCondConfig.class);
                conditionResources.forEach(StrengthCondConfig::getAndCreateStrenConditions);
                strenConditions = conditionResources;
            }
        }
        return strenConditions;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EffectType getEffectType() {
        return effectType;
    }

    public void setEffectType(EffectType effectType) {
        this.effectType = effectType;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMaxOverlyTime() {
        return maxOverlyTime;
    }

    public void setMaxOverlyTime(int maxOverlyTime) {
        this.maxOverlyTime = maxOverlyTime;
    }

    public int getMaxOverlyCount() {
        return maxOverlyCount;
    }

    public void setMaxOverlyCount(int maxOverlyCount) {
        this.maxOverlyCount = maxOverlyCount;
    }

//    public int getProvokeProb() {
//        return provokeProb;
//    }
//
//    public void setProvokeProb(int provokeProb) {
//        this.provokeProb = provokeProb;
//    }
//
//    public EffectTriggerType getProvokeType() {
//        return provokeType;
//    }
//
//    public void setProvokeType(EffectTriggerType provokeType) {
//        this.provokeType = provokeType;
//    }


    public int getDelayRound() {
        return delayRound;
    }

    public void setDelayRound(int delayRound) {
        this.delayRound = delayRound;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public boolean isDeadRemove() {
        return deadRemove;
    }

    public void setDeadRemove(boolean deadRemove) {
        this.deadRemove = deadRemove;
    }

    public boolean isAddBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

    public boolean isAfterUseRemove() {
        return afterUseRemove;
    }

    public boolean getAfterUseRemove() {
        return afterUseRemove;
    }

    public void setAfterUseRemove(boolean afterUseRemove) {
        this.afterUseRemove = afterUseRemove;
    }

    public String getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public int getMaxTimes() {
        return maxTimes;
    }

    public void setMaxTimes(int maxTimes) {
        this.maxTimes = maxTimes;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getStrengthCondition() {
        return strengthCondition;
    }

    public void setStrengthCondition(String strengthCondition) {
        this.strengthCondition = strengthCondition;
    }


    public boolean isTriggerbroadcast() {
        return triggerbroadcast;
    }

    public void setTriggerbroadcast(boolean triggerbroadcast) {
        this.triggerbroadcast = triggerbroadcast;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Map<TargetType, String> getTargets() {
        return targets;
    }

    public void setTargets(Map<TargetType, String> targets) {
        this.targets = targets;
    }

    public int getAbilityPower() {
        return abilityPower;
    }

    public void setAbilityPower(int abilityPower) {
        this.abilityPower = abilityPower;
    }

    public boolean isEffectByPower() {
        return abilityPower > 0;
    }

    public int getStrengthValueType() {
        return strengthValueType;
    }

    public void setStrengthValueType(int strengthValueType) {
        this.strengthValueType = strengthValueType;
    }

    public boolean isStrengthValueRate() {
        return this.strengthValueType == 1;
    }

    public int getBuffType() {
        return buffType;
    }

    public void setBuffType(int buffType) {
        this.buffType = buffType;
    }

    public boolean isValueRate() {
        return valueType == 2;
    }

    public int getMaxTarget() {
        return maxTarget;
    }

    public void setMaxTarget(int maxTarget) {
        this.maxTarget = maxTarget;
    }

    /**
     * 是否受法强影响
     *
     * @return
     */
    public boolean isMagicStrength() {
        return abilityPower != 0;
    }

    public RoundStage getRemoveStage() {
        return removestage;
    }

    public void setRemoveStage(RoundStage removeStage) {
        this.removestage = removestage;
    }

    public int getCamp() {
        return camp;
    }

    public void setCamp(int camp) {
        this.camp = camp;
    }

    public boolean limitSelfTurn() {
        return camp == 0;
    }

    public boolean limitEnemyTurn() {
        return camp == 1;
    }

    public boolean noLimitTurn() {
        return camp == 2;
    }

    public String getRejectEffectStr() {
        return rejectEffectStr;
    }

    public void setRejectEffectStr(String rejectEffectStr) {
        this.rejectEffectStr = rejectEffectStr;
    }

    public boolean rejectEffect(EffectType effectType) {
        if (Objects.isNull(rejectEffects)) {
            synchronized (this) {
                if (Objects.isNull(rejectEffects)) {

                    if (StringUtils.isEmpty(rejectEffectStr)) {
                        rejectEffects = Collections.EMPTY_SET;
                    } else {
                        rejectEffects = new HashSet<>(JsonUtils.string2List(rejectEffectStr, EffectType.class));
                    }
                }
            }
        }
        return rejectEffects.contains(effectType);
    }

    public boolean isRemoveUseSkill() {
        return removeUseSkill;
    }

    public void setRemoveUseSkill(boolean removeUseSkill) {
        this.removeUseSkill = removeUseSkill;
    }

    public boolean isRemoveBeAttack() {
        return removeBeAttack;
    }

    public void setRemoveBeAttack(boolean removeBeAttack) {
        this.removeBeAttack = removeBeAttack;
    }
}
