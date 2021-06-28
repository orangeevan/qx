package com.mmorpg.qx.module.skill.model;

import com.haipaite.common.event.core.EventBusManager;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.condition.ConditionManager;
import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActSelector;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.skill.event.UseSkillEvent;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.cd.CDContainer;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.model.effect.EffectType;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.model.target.TargetManager;
import com.mmorpg.qx.module.skill.packet.vo.SkillVo;
import com.mmorpg.qx.module.skill.packet.vo.TrainerBuildCDVo;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.skill.service.SkillService;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author wang ke
 * @description: 技能控制器
 * @since 20:44 2020-08-05
 */
public final class SkillController {
    /**
     * 拥有者
     */
    private AbstractCreature owner;

    /**
     * 技能冷却器
     */
    private CDContainer skillCD;

    /**
     * 正在施法技能
     */
    private Skill castSkill;

    /**
     * 玩家技能池
     */
    private List<SkillResource> skills;

    /**
     * 技能释放条件
     */
    private Map<Integer, Conditions> skillConditions;

    public SkillController(AbstractCreature owner) {
        this.owner = owner;
        skills = new ArrayList<>(6);
        skillCD = new CDContainer();
        skillConditions = new HashMap<>();
    }

    public CDContainer getSkillCD() {
        return skillCD;
    }

    public void setSkillCD(CDContainer skillCD) {
        this.skillCD = skillCD;
    }

    public Skill getCastSkill() {
        return castSkill;
    }

    public void setCastSkill(Skill castSkill) {
        this.castSkill = castSkill;
    }

    public AbstractCreature getOwner() {
        return owner;
    }


    /***
     * 添加新技能
     * @param skillId
     */
    public void addSkill(int skillId) {
        SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
        Conditions conditions = ConditionManager.createConditions(skillResource.getUseConditionsList());
        skills.add(skillResource);
        Collections.sort(skills);
        skillConditions.put(skillId, conditions);
    }

    /***
     * 检查技能能否使用
     * @param skill
     * @return
     */
    public int canUseSkill(Skill skill) {
        //驯养师是否在战斗场景
        if (!skill.getSkillCaster().isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        AbstractCreature skillCaster = skill.getSkillCaster();
        if (skillCaster.getEffectController().isInStatus(EffectStatus.Limit_Use_Skill)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        Room room = skillCaster.getMaster().getRoom();
        //驯养师状态检查
        if (room.getCurrentTurn() == skillCaster.getMaster() && RelationshipUtils.isPlayerTrainer(skillCaster.getMaster())) {
            AbstractRSActSelector actByRoundStage = RoundFightService.getInstance().getActByObjectType(room.getRoundStage(), ObjectType.PLAYER_TRAINER);
            short packet = PacketId.USE_SKILL_REQ;
            if (skill.isBuildingSkill()) {
                packet = PacketId.USE_BUILDING_SKILL_REQ;
            }
            if (!actByRoundStage.isActionAccept(packet)) {
                throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
            }
        }
        //检查目标及施法距离
        if (Objects.nonNull(skill.getTarget()) && !CollectionUtils.isEmpty(skill.getTarget().getTargetIds())) {
            WorldMapInstance mapInstance = skill.getSkillCaster().getWorldMapInstance();
            for (long targetId : skill.getTarget().getTargetIds()) {
                AbstractCreature targetById = mapInstance.getCreatureById(targetId);
                if (Objects.isNull(targetById)) {
                    throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
                }
                if (!BeanService.getBean(SkillService.class).isInSkillRange(skill.getSkillCaster(), targetById, skill.getResource())) {
                    throw new ManagedException(ManagedErrorCode.SKILL_NOT_IN_RANGE);
                }
            }
        }
        //检查CD
        if (skillCD.isSkillCD(skill.getResource().getSkillId(), room.getRound())) {
            return ManagedErrorCode.SKILL_CD;
        }
        SkillResource skillResource = SkillManager.getInstance().getSkillResource(skill.getResource().getSkillId());
        int costMp = skillResource.getCostMp();
        int costGold = skillResource.getCostGold();
        //是否处于操作费用加倍状态,并且不处于减免状态
        if (this.getOwner().getMaster().getEffectController().isInStatus(EffectStatus.Operate_Cost) && !this.getOwner().getEffectController().isInStatus(EffectStatus.Build_Op_Reduce_Cost)) {
            Effect effect = this.getOwner().getMaster().getEffectController().getEffect(EffectType.Operate_Cost);
            if (effect.getEffectResource().isValueRate()) {
                costMp += costMp * effect.getValue() * 1.0f / 10000;
                costGold += costGold * effect.getValue() * 1.0f / 10000;
            } else {
                costMp += effect.getValue();
                costGold += effect.getValue();
            }
        }
        //检查魔法
        if (skillResource.getCostMp() > 0 && skillCaster.getMaster().getLifeStats().getCurrentMp() < costMp) {
            return ManagedErrorCode.SKILL_MAGIC_LACK;
        }
        //检查金币
        if (skillResource.getCostGold() > 0 && skillCaster.getMaster().getLifeStats().getCurrentGold() < costGold) {
            return ManagedErrorCode.SKILL_GOLD_LACK;
        }

        if (skillCaster.isAlreadyDead()) {
            return ManagedErrorCode.DEAD_ERROR;
        }
        //检查施法条件
        if (!CollectionUtils.isEmpty(skillConditions)) {
            Conditions conditions = skillConditions.get(skill.getResource().getSkillId());
            if (Objects.nonNull(conditions)) {
                Result result = conditions.verify(skill, null, 1);
                if (!result.isSuccess()) {
                    return ManagedErrorCode.SKILL_CONDITIONS_LIMIT;
                }
            }
        }
        return ManagedErrorCode.OKAY;
    }

    /***
     * 使用技能
     * @param skill
     * @return
     */
    public List<AbstractSkillResult> useSkill(Skill skill) {
        AbstractCreature creature = skill.getSkillCaster();
        List<AbstractSkillResult> results = skill.useSkill(1);
        creature.getMaster().getRoom().addUseSkillTimeRound(creature.getObjectId());
        creature.handleEffect(TriggerType.MWN_USE_SKILL_TIME_ROUND, creature);
        return results;
    }

    /**
     * 使用完技能后，处理技能效果,目的是为了先把技能伤害播放完再添加效果，与 useSkill方法成对使用
     *
     * @param skill
     */
    public void afterUseSkill(Skill skill) {
        // 添加技能对应效果,效果目标要跟技能目标一致
        SkillResource resource = skill.getResource();
        if (!CollectionUtils.isEmpty(resource.getEffects())) {
            SkillManager.getInstance().addEffects(skill.getSkillCaster(), resource.getEffects(), resource.getSkillId(), skill.getTarget());
        }
        skill.getSkillCaster().getEffectController().unsetStatus(EffectStatus.Invisible, true);
        EventBusManager.getInstance().syncSubmit(UseSkillEvent.valueOf(skill));
    }

    /**
     * 魔物娘战斗技能选择，后续业务复杂可能需要改
     *
     * @param target
     * @return
     */
    public Skill selectSkill(Target target) {
        for (SkillResource sr : skills) {
            if (this.skillCD.isSkillCD(sr.getSkillId(), this.owner.getMaster().getRoom().getRound())) {
                continue;
            }

            Skill skill = Skill.valueOf(sr, this.getOwner(), target);
            if (canUseSkill(skill) != ManagedErrorCode.OKAY) {
                continue;
            }
            Result chooserTarget = TargetManager.getInstance().judgeTarget(skill, target);
            if (chooserTarget.isFailure()) {
                continue;
            }
            return skill;
        }
        return null;
    }

    /***
     * 检查是否有该技能
     * @param skillId
     * @return
     */
    public boolean hasSkill(int skillId) {
        if (CollectionUtils.isEmpty(skills)) {
            return false;
        }
        return skills.stream().anyMatch(skillResource -> skillResource.getSkillId() == skillId);
    }

    /***
     * 检查是否有某类技能
     * @param skillType
     * @return
     */
    public boolean hasSkill(SkillType skillType) {
        if (CollectionUtils.isEmpty(skills)) {
            return false;
        }
        return skills.stream().anyMatch(skill -> skill.getSkillType() == skillType);
    }

    public List<SkillResource> getSkills() {
        return skills;
    }

    public List<SkillVo> getSkillVo() {
        List<SkillVo> vos = new ArrayList<>();
        if (CollectionUtils.isEmpty(skills)) {
            return vos;
        }
        skills.forEach(resource -> {
            int cd = this.skillCD.getCurPublicSkillCD(resource.getPublicGroup());
            if (cd > 0) {
                SkillVo vo = new SkillVo();
                vo.setSkillGroupId(resource.getPublicGroup());
                vo.setPubRoundCd(cd);
            }
        });
        return vos;
    }

    public List<TrainerBuildCDVo> getBuildVo() {
        return this.skillCD.getBuildVo();
    }

    /**
     * 获取单个技能cd
     *
     * @param skillId
     * @return
     */
    public int getSKillCD(int skillId) {
        return skillCD.getCurSkillCD(skillId);
    }

    /**
     * 获取技能组CD
     *
     * @param skillId
     * @return
     */
    public int getSkillGroupCD(int skillId) {
        SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
        if (skillResource.getPublicGroup() == 0) {
            return 0;
        }
        return skillCD.getCurPublicSkillCD(skillResource.getPublicGroup());
    }

    /**
     * 移除技能及CD
     *
     * @param skillId
     */
    public void removeSkill(int skillId) {
        if (!hasSkill(skillId)) {
            return;
        }
        SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
        skills.remove(skillResource);
        skillConditions.remove(skillId);
        skillCD.clearSkillAllCD(skillId);
    }


    public boolean replaceEvoSkill(int oldSkillId, int newSkillId) {
        if (!hasSkill(oldSkillId)) {
            return false;
        }
        SkillResource oldSKill = SkillManager.getInstance().getSkillResource(oldSkillId);
        SkillResource newSkill = SkillManager.getInstance().getSkillResource(newSkillId);
        if (Objects.isNull(newSkill)) {
            return false;
        }
        skills.remove(oldSKill);
        addSkill(newSkillId);
        int curSkillCD = skillCD.getCurSkillCD(oldSkillId);
        int curSkillGroupCD = skillCD.getCurPublicSkillCD(oldSKill.getPublicCDGroup());
        skillCD.clearSkillAllCD(oldSkillId);
        skillCD.initSkillCD(newSkillId, curSkillCD, curSkillGroupCD);
        return true;
    }
}
