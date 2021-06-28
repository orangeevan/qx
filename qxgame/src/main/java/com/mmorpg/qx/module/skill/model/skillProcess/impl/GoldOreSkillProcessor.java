package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.GoldOreSkillResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wang ke
 * @description: 金矿技能
 * @since 16:12 2020-09-02
 */
@Component
public class GoldOreSkillProcessor extends AbstractSkillProcessor<GoldOreSkillResult, Map<String, Integer>> {
    @Override
    public SkillType getSkillType() {
        return SkillType.Gold;
    }

    @Override
    public List<GoldOreSkillResult> process(Skill skill, int roundIndex) {
        List<AbstractCreature> denfendersList = skill.getDenfendersList();
        if (CollectionUtils.isEmpty(denfendersList)) {
            return null;
        }
        Map<String, Integer> param = initParam(skill.getResource());
        List<GoldOreSkillResult> results = new ArrayList<>();
        denfendersList.forEach(defender -> {
            int increase = param.get("value") * param.get("rate") / 10000 * roundIndex;
            increase = defender.getLifeStats().increaseGold(increase, Reason.Building, true);
            results.add(new GoldOreSkillResult(skill.getSkillCaster().getObjectId(), defender.getObjectId(), skill, increase, roundIndex));
            //PacketSendUtility.broadcastPacket(defender, LifeChangeResp.valueOf(Reason.Building, defender), true);
        });
        return results;
    }

    @Override
    public Map<String, Integer> initParam(SkillResource resource) {
        Map<String, Integer> params = JsonUtils.string2Map(resource.getParam(), String.class, Integer.class);
        return params;
    }
}
