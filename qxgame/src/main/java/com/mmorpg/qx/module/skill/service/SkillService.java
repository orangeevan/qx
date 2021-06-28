package com.mmorpg.qx.module.skill.service;

import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.building.manager.BuildingResourceManager;
import com.mmorpg.qx.module.building.resource.BuildingResource;
import com.mmorpg.qx.module.mwn.service.MWNService;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.*;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.object.gameobject.building.AbstractBuilding;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActSelector;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillController;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.model.effect.EffectType;
import com.mmorpg.qx.module.skill.model.effect.impl.LimitOpBuildEffect;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.packet.BuildingUseSkillReq;
import com.mmorpg.qx.module.skill.packet.EffectInfoResp;
import com.mmorpg.qx.module.skill.packet.UseSkillReq;
import com.mmorpg.qx.module.skill.packet.vo.EffectVo;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class SkillService {

    @Autowired
    private SkillManager skillManager;

    /***
     * 玩家使用技能
     * @param trainerCreature
     * @param req
     */
    public void useSkill(AbstractTrainerCreature trainerCreature, UseSkillReq req) {
        if (Objects.isNull(trainerCreature)) {
            return;
        }
        if (trainerCreature.getRoom().getCurrentTurn() != trainerCreature) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        //驯养师状态检查
        AbstractRSActSelector actByRoundStage = RoundFightService.getInstance().getActByObjectType(trainerCreature.getRoom().getRoundStage(), ObjectType.PLAYER_TRAINER);
        if (!actByRoundStage.isActionAccept(PacketId.USE_SKILL_REQ)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        AbstractCreature skillCaster = trainerCreature;
        SkillController skillController = null;
        //施法者
        if (trainerCreature.getObjectId() == req.getObjectId()) {
            skillController = trainerCreature.getSkillController();
        } else {
            skillCaster = trainerCreature.getMwn(req.getObjectId());

            if (Objects.isNull(skillCaster)) {
                throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
            }
            if (skillCaster.getEffectController().isInStatus(EffectStatus.Silence)) {
                throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
            }
            skillController = skillCaster.getSkillController();
        }
        //检查技能是否拥有
        if (!skillController.hasSkill(req.getSkillId())) {
            throw new ManagedException(ManagedErrorCode.SKILL_NOT_OWE);
        }

        Skill skill = Skill.valueOf(SkillManager.getInstance().getSkillResource(req.getSkillId()), skillCaster, req.getTarget());
        int result = skillController.canUseSkill(skill);
        if (result != ManagedErrorCode.OKAY) {
            throw new ManagedException(result);
        }

        List<AbstractSkillResult> results = skillController.useSkill(skill);
        RoundFightUtils.sendUseSkill(skillCaster, skill.getResource().getSkillId(), skill.getTarget() == null ? new ArrayList<>() : skill.getTarget().getTargetIds(), results, skill.getResource().isBroadcast());
        skillController.afterUseSkill(skill);
        //PacketSendUtility.broadcastPacket(trainerCreature, RoundFightReportResp.valueOf(results, trainerCreature.getObjectId()), true);
    }

    /***
     * 使用建筑技能
     * @param trainerCreature
     * @param req
     */
    public void buildingUseSkill(AbstractTrainerCreature trainerCreature, BuildingUseSkillReq req) {
        //检查玩家是否被限制操作建筑
        //检查建筑是否有对应技能
        AbstractVisibleObject building = trainerCreature.getWorldMapInstance().findObject(req.getBuildingId());
        if (Objects.isNull(building)) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        boolean inStatus = trainerCreature.getEffectController().isInStatus(EffectStatus.Limit_Op_Build);
        //被限制操作指定类型建筑
        if (inStatus) {
            Collection<Effect> effects = trainerCreature.getEffectController().getEffects(EffectType.Limit_OP_Build);
            if (!CollectionUtils.isEmpty(effects)) {
                long count = effects.stream().map(effect -> (LimitOpBuildEffect) effect.getEffectTemplate()).
                        filter(limitOpBuildEffect -> limitOpBuildEffect.getBuildType() == null || limitOpBuildEffect.getBuildType() == building.getObjectType()).count();
                if (count > 0) {
                    throw new ManagedException(ManagedErrorCode.LIMIT_OP);
                }
            }
        }
        SkillController skillController = trainerCreature.getSkillController();
        if (skillController.getSkillCD().isBuildCD(building.getObjectId(), trainerCreature.getRoom().getRound())) {
            throw new ManagedException(ManagedErrorCode.SKILL_CD);
        }
        if (!(building instanceof AbstractBuilding)) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        AbstractBuilding buildObject = (AbstractBuilding) building;
        BuildingResource buildingResource = BuildingResourceManager.getInstance().getResource(buildObject.getObjectKey());
        int skillId = buildingResource.getSkills().get(req.getSkillIdIIndex());
        Skill skill = Skill.valueOf(SkillManager.getInstance().getSkillResource(skillId), trainerCreature, req.getTarget());
        skill.setBuildingSkill(true);
        int result = skillController.canUseSkill(skill);
        if (result != ManagedErrorCode.OKAY && result != ManagedErrorCode.SKILL_CD) {
            throw new ManagedException(result);
        }
        if (!buildObject.getSkills().contains(skillId)) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        skill.setBuildCostRate(buildingResource.getInCdCostRate());
        //职业加倍消耗
        if (req.getJobTypeIndex() > 0) {
            Integer jobCostRate = buildingResource.getJobCostGold().get(req.getJobTypeIndex());
            skill.setBuildCostRate(skill.getBuildCostRate() + jobCostRate);
            AttrType attrType = AttrType.valueOf(req.getJobTypeIndex());
            //skill.setBuildSelectJobType(BuildingResourceManager.getInstance().getJobType(req.getJobTypeIndex()));
            skill.setBuildSelectJobType(attrType);
        }
        trainerCreature.getSkillController().getSkillCD().updateBuildCD(buildObject.getObjectId(), buildingResource.getBuildingCd());
        skill.setBuildCostGold(buildingResource.getCostGold().get(req.getSkillIdIIndex()));
        List<AbstractSkillResult> results = skillController.useSkill(skill);
        RoundFightUtils.sendUseSkill(skill.getSkillCaster(), skill.getResource().getSkillId(), skill.getTarget() == null ? new ArrayList<Long>() : skill.getTarget().getTargetIds(), results, true);
        skillController.afterUseSkill(skill);
    }


    /***
     * 请求自身或魔物娘身上效果信息
     * @param trainerCreature
     * @param objectId
     */
    public void selfMwnEffectInfo(PlayerTrainerCreature trainerCreature, long objectId) {
        List<EffectVo> effectVos = null;
        if (trainerCreature.getObjectId() == objectId) {
            effectVos = trainerCreature.getEffectController().toEffectVo();
        } else {
            Collection<MWNCreature> aliveMwns = trainerCreature.getMWN(true);
            if (!CollectionUtils.isEmpty(aliveMwns)) {
                Optional<MWNCreature> any = aliveMwns.stream().filter(mwnCreature -> mwnCreature.getObjectId() == objectId).findAny();
                if (any.isPresent()) {
                    effectVos = any.get().getEffectController().toEffectVo();
                }
            }
        }
        if (Objects.isNull(effectVos)) {
            return;
        }
        EffectInfoResp effectInfoResp = EffectInfoResp.valueOf(objectId, effectVos);
        PacketSendUtility.sendPacket(trainerCreature, effectInfoResp);
    }

    /**
     * 判断目标是否在技能施法范围内
     *
     * @param skillCaster
     * @param target
     * @param skillResource
     * @return
     */
    public boolean isInSkillRange(AbstractCreature skillCaster, AbstractCreature target, SkillResource skillResource) {
        //驯养师等不考虑攻击范围
        if (!RelationshipUtils.isMWN(skillCaster)) {
            return true;
        }
        return MWNService.getInstance().isInDomainRange(RelationshipUtils.toMWNCreature(skillCaster), target);
    }
}
