package com.mmorpg.qx.module.skill.model.effect.impl;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author wang ke
 * @description: 满足条件触发添加新效果
 * @since 10:34 2020-11-05
 */
public class AddNewEffect extends AbstractEffectTemplate {

    private List<Integer> addEffects;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        addEffects = JsonUtils.string2List(resource.getParam(),Integer.class);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        if (CollectionUtils.isEmpty(addEffects)) {
            return false;
        }
        chooseTargets(effect);
        Set<AbstractCreature> targets = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(targets)) {
            SkillManager.getInstance().addEffects(effect.getEffected(), addEffects, effect.getSkillResourceId(), effect.getEffectTarget());
            return true;
        }
        //SkillManager.getInstance().addEffects(effect.getEffected(), addEffects, effect.getSkillResourceId(), effect.getTarget());
        return false;
    }


}

