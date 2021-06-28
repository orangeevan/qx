package com.mmorpg.qx.module.skill.model;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.model.effect.EffectType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.model.target.TargetManager;
import com.mmorpg.qx.module.skill.model.target.TargetSelectType;
import com.mmorpg.qx.module.skill.packet.SingleSkillCdResp;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Skill {

    /**
     * 效果承受着
     */
    private List<AbstractCreature> denfendersList;

    /**
     * 施法者
     */
    private AbstractCreature skillCaster;

    /**
     * 配置表id
     */
    private int skillId;

    /**
     * 目标
     */
    private Target target;

    /**
     * 建筑技能操作相关
     */
    private transient boolean isBuildingSkill;

    private int buildCostRate;

    private int buildCostGold;

    private AttrType buildSelectJobType;

    public static Skill valueOf(SkillResource skillResource, AbstractCreature attackers, Target target) {
        Skill skill = new Skill();
        skill.skillId = skillResource.getSkillId();
        skill.skillCaster = attackers;
        skill.target = target;
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(target, skill.skillCaster.getWorldMapInstance());
        if (Objects.nonNull(creatures)) {
            skill.setDenfendersList(new ArrayList<>(creatures));
        }
        return skill;
    }

    public SkillResource getResource() {
        return SkillManager.getInstance().getSkillResource(skillId);
    }

    /**
     * 技能释放
     *
     * @param roundIndex
     * @return
     */
    public List<AbstractSkillResult> useSkill(int roundIndex) {

        SkillResource resource = getResource();
        Conditions conditions = resource.getAndCreateConditions();
        //检查技能施法条件
        Result useVerify = conditions.verify(this, null, 0);
        if (useVerify.isFailure()) {
            System.err.println("技能验证失败: " + skillId);
            throw new ManagedException(ManagedErrorCode.SKILL_CONDITIONS_LIMIT);
        }

        //目标选择器验证
        if (this.getResource().getTargetSelectType() == TargetSelectType.Fix) {
            Result result = TargetManager.getInstance().judgeTarget(this, target);
            if (result.isFailure()) {
                throw new ManagedException(ManagedErrorCode.SKILL_TARGET_ERROR);
            }
        } else {
            Target target = TargetManager.getInstance().chooseTarget(new ArrayList<>(this.getResource().getTargets().keySet()), this.getSkillCaster());
            if (target != null && !CollectionUtils.isEmpty(target.getTargetIds())) {
                //目标数为0全体
                if (getResource().getMaxTarget() > 0) {
                    if (target.getTargetIds().size() > getResource().getMaxTarget()) {
                        List<Long> subList = target.getTargetIds().subList(0, getResource().getMaxTarget() - 1);
                        target.setTargetIds(subList);
                    }
                }
                this.target = target;
                Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(target, this.skillCaster.getWorldMapInstance());
                if (Objects.nonNull(creatures)) {
                    setDenfendersList(new ArrayList<>(creatures));
                }
            } else {
                throw new ManagedException(ManagedErrorCode.SKILL_TARGET_ERROR);
            }
        }

        // 施法时不能移动
        if (skillCaster != null && skillCaster.getMoveController().isMoving() && !this.isBuildingSkill()) {
            throw new ManagedException(ManagedErrorCode.SKILL_NOT_MOVE);
        }

        float costMultiple = 1;
        int addValue = 0;
        //建筑技能处于cd消耗翻倍
        if (this.isBuildingSkill() && skillCaster.getSkillController().getSkillCD().isSkillCD(skillId, roundIndex)) {
            costMultiple = GameUtil.toRatio10000(this.getBuildCostRate());
        }

        //是否处于操作费用加倍状态
        if (this.skillCaster.getEffectController().isInStatus(EffectStatus.Operate_Cost)) {
            Effect effect = this.skillCaster.getEffectController().getEffect(EffectType.Operate_Cost);
            if (effect.getEffectResource().isValueRate()) {
                costMultiple += GameUtil.toRatio10000(effect.getValue());
            } else {
                addValue = effect.getValue();
            }
        }
        //扣除金币
        if (this.isBuildingSkill) {
            int costGold = Math.round(this.getBuildCostGold() * costMultiple) + addValue;
            //建筑技能消耗金币
            if (costGold > 0) {
                if (skillCaster.getMaster().getLifeStats().getCurrentGold() < costGold) {
                    throw new ManagedException(ManagedErrorCode.NOT_ENOUGH_GOLD);
                }
                skillCaster.getLifeStats().reduceGold(costGold, Reason.Building, false);
            }
        } else {
            if (!skillCaster.getEffectController().isInStatus(EffectStatus.Charge)) {
                //扣除技能魔法
                int costMp = Math.round(resource.getCostMp() * costMultiple + addValue);
                //超过10特殊处理
                costMp = Math.min(costMp, RoundFightUtils.ROUND_MAX_MP);
                if (costMp > 0) {
                    skillCaster.getMaster().getLifeStats().reduceMp(costMp, Reason.Trainer_Skill_Effect, true, true);
                }
            }
        }

        //添加技能CD
        skillCaster.getSkillController().getSkillCD().updateSkillCD(skillId, resource.getCd());
//        if (this.getResource().isDisplay()) {
//            UseSkillResp useSkillResp = new UseSkillResp();
//            useSkillResp.setObjectId(getSkillCaster().getObjectId());
//            useSkillResp.setSkillId(skillId);
//            useSkillResp.setTargetIds(target.getTargetIds());
//            // 通知前端开始施法
//            PacketSendUtility.sendPacket(getSkillCaster(), useSkillResp);
//            PacketSendUtility.broadcastPacket(denfendersList, useSkillResp);
//        }
        //技能释放CD同步
        PacketSendUtility.sendPacket(getSkillCaster().getMaster(), SingleSkillCdResp.valueOf(skillCaster, skillId));
        // 添加技能对应效果,效果目标要跟技能目标一致
        if (!CollectionUtils.isEmpty(resource.getEffects())) {
            SkillManager.getInstance().addEffects(skillCaster, resource.getEffects(), skillId, this.target);
        }

        //技能释放动作触发行为
        skillCaster.getController().onCastSkill(this);

        //技能效果处理
        SkillType skillType = resource.getSkillType();
        AbstractSkillProcessor skillProcessor = AbstractSkillProcessor.getSkillProcessor(skillType);
        List<AbstractSkillResult> results = skillProcessor.process(this, roundIndex);

        return results;
    }

    public AbstractCreature getSkillCaster() {
        return skillCaster;
    }

    public Target getTarget() {
        return target;
    }

    public List<AbstractCreature> getDenfendersList() {
        return denfendersList;
    }

    public void setDenfendersList(List<AbstractCreature> denfendersList) {
        this.denfendersList = denfendersList;
    }

    public boolean isBuildingSkill() {
        return isBuildingSkill;
    }

    public void setBuildingSkill(boolean buildingSkill) {
        isBuildingSkill = buildingSkill;
    }

    public int getBuildCostRate() {
        return buildCostRate;
    }

    public void setBuildCostRate(int buildCostRate) {
        this.buildCostRate = buildCostRate;
    }

    public int getBuildCostGold() {
        return buildCostGold;
    }

    public void setBuildCostGold(int buildCostGold) {
        this.buildCostGold = buildCostGold;
    }

    public AttrType getBuildSelectJobType() {
        return buildSelectJobType;
    }

    public void setBuildSelectJobType(AttrType buildSelectJobType) {
        this.buildSelectJobType = buildSelectJobType;
    }

}
