package com.mmorpg.qx.module.skill.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;
import com.mmorpg.qx.module.condition.ConditionManager;
import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.mmorpg.qx.module.skill.model.SkillReleaseType;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.target.TargetSelectType;
import com.mmorpg.qx.module.skill.model.target.TargetType;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 技能配置表, order越大，优先释放，被动技能优先
 *
 * @author wang ke
 * @since v1.0 2018年3月1日
 */
@Resource
public class SkillResource implements Comparable<SkillResource> {
    @Id
    private int skillId;
    /**
     * effectResource的ID集合
     */
    private List<Integer> effects;

    private String useConditions;
    /**
     * 使用条件
     */
    private List<ConditionResource> useConditionsList;
    /**
     * 冷却回合
     */
    private int cd;
    /**
     * 目标类型,对应参数数值
     */
    //private List<TargetType> targetType;
    private Map<TargetType, String> targets;

    private String targetType;

    /**
     * 释放消耗魔法
     */
    private int costMp;
    /**
     * 消耗金币
     */
    private int costGold;
    /**
     * 施法距离
     */
    private int range;

    /**
     * 抬手播放的时间,毫秒
     */
    private int delayAction;

    /**
     * 最多攻击目标数量
     */
    private int maxTarget;
    /**
     * 公共CD组
     */
    private int publicCDGroup;

    /**
     * 是否播放攻击动作
     */
    private boolean broadcast = true;

    /**
     * 使用条件
     */
    private transient Conditions conditions;

    /**
     * 公共技能组
     */
    private int publicGroup;

    /**
     * 被动技能
     */
    private boolean passive;

    /***
     * 技能释放顺序越大优先级越高
     */
    private int order;

    /**
     * 技能基础伤害
     */
    private int basicDamage;

    /**
     * 技能类型
     */
    private SkillType skillType;

    /**
     * 技能参数,每个类型参数可以不同，根据具体技能类型解析
     */
    private String param;

    private int skillReleaseType;

    /**
     * 法强影响 0不受 1受影响
     */
    private int abilityPower;

    /**
     * 值类型
     */
    private int valueType;
    /**
     * 在对目标类型进行筛选时是指定目标还是随机目标
     * 0：随机
     * 1：指定
     */
    private int targetSelect;


    /**
     * 正负面标识
     */
    private int positiveMark;
    /**
     * 元素标识
     */
    private int eleMark;
    /**
     * 职业标识
     */
    private int jobMark;

    public Conditions getAndCreateConditions() {
        if (conditions == null) {
            if (CollectionUtils.isEmpty(useConditionsList)) {
                conditions = new Conditions();
            } else {
                conditions = ConditionManager.createConditions(useConditionsList);
            }
        }
        return conditions;
    }


    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public List<Integer> getEffects() {
        return effects;
    }

    public void setEffects(List<Integer> effects) {
        this.effects = effects;
    }

    public int getCd() {
        return cd;
    }

    public void setCd(int cd) {
        this.cd = cd;
    }

//    public List<TargetType> getTargetType() {
//        return targetType;
//    }
//
//    public void setTargetType(List<TargetType> targetType) {
//        this.targetType = targetType;
//    }


    public Map<TargetType, String> getTargets() {
        return targets;
    }

    public void setTargets(Map<TargetType, String> targets) {
        this.targets = targets;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getMaxTarget() {
        return maxTarget;
    }

    public void setMaxTarget(int maxTarget) {
        this.maxTarget = maxTarget;
    }

    public int getPublicCDGroup() {
        return publicCDGroup;
    }

    public void setPublicCDGroup(int publicCDGroup) {
        this.publicCDGroup = publicCDGroup;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public int getCostMp() {
        return costMp;
    }

    public void setCostMp(int costMp) {
        this.costMp = costMp;
    }

    public List<ConditionResource> getUseConditionsList() {
        return useConditionsList;
    }

    public void setUseConditionsList(List<ConditionResource> useConditionsList) {
        this.useConditionsList = useConditionsList;
    }

    public int getDelayAction() {
        return delayAction;
    }

    public void setDelayAction(int delayAction) {
        this.delayAction = delayAction;
    }

    public String getUseConditions() {
        return useConditions;
    }

    public void setUseConditions(String useConditions) {
        this.useConditions = useConditions;
    }

    public int getPublicGroup() {
        return publicGroup;
    }

    public void setPublicGroup(int publicGroup) {
        this.publicGroup = publicGroup;
    }

    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getBasicDamage() {
        return basicDamage;
    }

    public void setBasicDamage(int basicDamage) {
        this.basicDamage = basicDamage;
    }

    public int getCostGold() {
        return costGold;
    }

    public void setCostGold(int costGold) {
        this.costGold = costGold;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getSkillReleaseType() {
        return skillReleaseType;
    }

    public void setSkillReleaseType(int skillReleaseType) {
        this.skillReleaseType = skillReleaseType;
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

    public boolean isValueRate() {
        return valueType == 2;
    }

    public int getTargetSelect() {
        return targetSelect;
    }

    public void setTargetSelect(int targetSelect) {
        this.targetSelect = targetSelect;
    }

    public boolean isRandomTarget() {
        return targetSelect == 0;
    }

    public boolean isFixTarget() {
        return targetSelect == 1;
    }

    public SkillReleaseType getReleaseType() {
        return SkillReleaseType.valueOf(skillReleaseType);
    }

    public TargetSelectType getTargetSelectType() {
        return TargetSelectType.valueOf(targetSelect);
    }

    public int getValueType() {
        return valueType;
    }

    public void setValueType(int valueType) {
        this.valueType = valueType;
    }

    public int getPositiveMark() {
        return positiveMark;
    }

    public void setPositiveMark(int positiveMark) {
        this.positiveMark = positiveMark;
    }

    public int getEleMark() {
        return eleMark;
    }

    public void setEleMark(int eleMark) {
        this.eleMark = eleMark;
    }

    public int getJobMark() {
        return jobMark;
    }

    public void setJobMark(int jobMark) {
        this.jobMark = jobMark;
    }

    @Override
    public int compareTo(SkillResource o) {
        if (this.order < o.getOrder()) {
            return 1;
        } else if (this.order > o.getOrder()) {
            return -1;
        } else {
            if (this.isPassive()) {
                return -1;
            }
            if (o.isPassive()) {
                return 1;
            }
            return 0;
        }
    }
}
