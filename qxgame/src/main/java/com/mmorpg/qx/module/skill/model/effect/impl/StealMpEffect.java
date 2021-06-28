package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;

/**
 * @author wang ke
 * @description:偷取魔法
 * @since 17:49 2020-10-27
 */
public class StealMpEffect extends AbstractEffectTemplate {

    @Override
    public boolean applyEffect(Effect effect) {
        AbstractCreature trigger = effect.getTrigger();
        if (!RelationshipUtils.isTrainer(trigger)) {
            return false;
        }
        int value = effect.getValue();
        trigger.getLifeStats().reduceMp(value, Reason.Mwn_Skill_Effect, true, false);
        effect.getEffector().getMaster().getLifeStats().increaseMp(value, Reason.Mwn_Skill_Effect, true, false);
        return true;
    }
}
