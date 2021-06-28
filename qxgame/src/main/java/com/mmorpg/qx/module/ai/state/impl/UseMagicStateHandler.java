package com.mmorpg.qx.module.ai.state.impl;

import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.model.target.TargetManager;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: AI使用技能
 * @since 16:46 2020-08-18
 */
public class UseMagicStateHandler implements IStateHandler {
    @Override
    public AIState getState() {
        return AIState.Use_Magic;
    }

    @Override
    public void handleState(AbstractTrainerCreature owner, Object... params) {
        List<SkillResource> skills = owner.getSkillController().getSkills();
        if (CollectionUtils.isEmpty(skills)) {
            return;
        }
        skills.stream().forEach(skillResource -> {
            //TargetType targetType = skillResource.getTargetType();
            try {
                if (CollectionUtils.isEmpty(skillResource.getTargets())) {
                    return;
                }
                Target target = TargetManager.getInstance().chooseTarget(skillResource.getTargets().keySet().stream().collect(Collectors.toList()), owner);
                if (Objects.isNull(target)) {
                    return;
                }
                Skill useSkill = owner.getSkillController().selectSkill(target);
                if (Objects.isNull(useSkill)) {
                    return;
                }
                int result = owner.getSkillController().canUseSkill(useSkill);
                if (result != ManagedErrorCode.OKAY) {
                    return;
                }
                List<AbstractSkillResult> results = owner.getSkillController().useSkill(useSkill);
                RoundFightUtils.sendUseSkill(useSkill.getSkillCaster(), useSkill.getResource().getSkillId(), useSkill.getTarget() == null ? new ArrayList<Long>() : useSkill.getTarget().getTargetIds(), results, true);
                owner.getSkillController().afterUseSkill(useSkill);
            } catch (Exception e) {
                //释放技能所有异常抛弃
            }
        });
    }
}
