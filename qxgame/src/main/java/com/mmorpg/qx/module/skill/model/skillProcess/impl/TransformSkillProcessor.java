package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.DisplaySkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.trainer.manager.TrainerManager;
import com.mmorpg.qx.module.trainer.resource.AITrainerResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang ke
 * @description: 变身技能
 * @since 20:04 2020-09-10
 */
public class TransformSkillProcessor extends AbstractSkillProcessor<DisplaySkillResult, Object> {

    @Override
    public SkillType getSkillType() {
        return SkillType.Transform;
    }

    /***
     * 机器人驯养师变身，属性，技能重置
     * @param skill
     * @param roundIndex
     * @return
     */
    @Override
    public List<DisplaySkillResult> process(Skill skill, int roundIndex) {
        List<DisplaySkillResult> results = new ArrayList<>();
        AbstractCreature caster = skill.getSkillCaster();
        if (!(caster instanceof RobotTrainerCreature)) {
            return results;
        }
        RobotTrainerCreature robotTrainer = RelationshipUtils.toRobotTrainer(caster);
        int resourceId = Integer.parseInt(skill.getResource().getParam());
        AITrainerResource trainerResource = TrainerManager.getInstance().getAITrainerResource(resourceId);
        robotTrainer.init(trainerResource);
        robotTrainer.initAttr();
//        UseSkillResp useSkillResp = new UseSkillResp();
//        useSkillResp.setObjectId(robotTrainer.getObjectId());
//        useSkillResp.setSkillId(skill.getResource().getSkillId());
//        Target target = Target.valueOf(robotTrainer.getGridId(), robotTrainer.getObjectId());
//        useSkillResp.setTargetIds(target.getTargetIds());
//        PacketSendUtility.broadcastPacketInWorldMap(robotTrainer, useSkillResp, true);
        results.add(DisplaySkillResult.valueOf(robotTrainer.getObjectId(), skill));
        return results;
    }

    @Override
    public Object initParam(SkillResource resource) {
        return null;
    }
}
