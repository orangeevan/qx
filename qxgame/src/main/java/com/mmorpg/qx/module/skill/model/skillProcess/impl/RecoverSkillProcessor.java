package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.RecoverSkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wang ke
 * @description:
 * @since 20:44 2020-08-31
 */
@Component
public class RecoverSkillProcessor extends AbstractSkillProcessor<RecoverSkillResult, Map<String, Integer>> {

    @Override
    public SkillType getSkillType() {
        return SkillType.Recover;
    }

    @Override
    public List<RecoverSkillResult> process(Skill skill, int roundIndex) {
        List<AbstractCreature> denfendersList = skill.getDenfendersList();
        if (CollectionUtils.isEmpty(denfendersList)) {
            return null;
        }
        Map<String, Integer> param = initParam(skill.getResource());
        List<RecoverSkillResult> results = new ArrayList<>();
        denfendersList.forEach(defender -> {
            if (defender.getLifeStats().isFullyRestoredHpMp()) {
                return;
            }
            int recoverHp = param.get("Hp");
            //魔法强度增强恢复系效果
            if (defender.getEffectController().isInStatus(EffectStatus.Magicstren_Inc_Rec)) {
                recoverHp += Math.round(200 * defender.getMaster().getMagicStrength());
            }
            int recoverMp = Math.round(param.get("Mp"));
            recoverHp = defender.getLifeStats().increaseHp(recoverHp, Reason.Trainer_Skill_Effect, false, false);
            recoverMp = defender.getLifeStats().increaseMp(recoverMp, Reason.Trainer_Skill_Effect, false, false);
            String format = String.format("恢复技能 【%s】, 恢复血量 【%s】 恢复对象 【%s】", skill.getResource().getSkillId(), recoverHp, defender.getObjectId());
            System.err.println(format);
            RecoverSkillResult result = new RecoverSkillResult(skill.getSkillCaster().getObjectId(), defender.getObjectId(), skill, recoverHp, recoverMp);
            result.setRoundIndex(roundIndex);
            results.add(result);
        });
        return results;
    }

    @Override
    public Map<String, Integer> initParam(SkillResource resource) {
        Map<String, Integer> params = JsonUtils.string2Map(resource.getParam(), String.class, Integer.class);
        return params;
    }
}
