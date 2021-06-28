package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.DisplaySkillResult;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author wang ke
 * @description: 强袭技能, 给己方所有场上魔物娘添加强袭buff
 * @since 13:46 2020-10-29
 */
public class AssaultAttackSkillProcessor extends AbstractSkillProcessor<DisplaySkillResult, Object> {


    @Override
    public SkillType getSkillType() {
        return SkillType.Assault_Attack;
    }

    @Override
    public List<DisplaySkillResult> process(Skill skill, int roundIndex) {
        List<DisplaySkillResult> results = new ArrayList<>();
        List<Integer> effects = skill.getResource().getEffects();
        EffectResource assaultResource = SkillManager.getInstance().getEffectResource(effects.get(0));
        AbstractTrainerCreature trainerCreature = skill.getSkillCaster().getMaster();
        //强袭技能针对指定类型魔物娘释放，比如游侠类魔物娘
        String attrName = skill.getResource().getParam();
        AttrType attrType = AttrType.valueOf(attrName);
        Collection<MWNCreature> aliveMwns = trainerCreature.getMWN(true);
        if (!CollectionUtils.isEmpty(aliveMwns)) {
            aliveMwns.forEach(mwn -> {
                if (mwn.getAttrController().hasAttr(attrType)) {
                    Effect effect = new Effect(skill.getSkillCaster(), mwn, assaultResource.getEffectType().create(), skill.getResource().getSkillId(), assaultResource.getId(),
                            Target.valueOf(mwn.getGridId(), mwn.getObjectId()));
                    mwn.getEffectController().addEffect(effect);
                    results.add(DisplaySkillResult.valueOf(mwn.getObjectId(), skill));
                }
            });
        }
        return results;
    }

    @Override
    public Object initParam(SkillResource resource) {
        return null;
    }
}
