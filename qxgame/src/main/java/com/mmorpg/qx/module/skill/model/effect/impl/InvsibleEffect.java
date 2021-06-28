package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;

/**
 * 隐身
 *
 * @author: yuanchengyan
 * @description:
 * @since 14:07 2021/4/12
 */
public class InvsibleEffect extends AbstractEffectTemplate {

    @Override
    public boolean applyEffect(Effect effect) {
        AbstractCreature creature = effect.getEffected();
        creature.getEffectController().setStatus(EffectStatus.Invisible, true);
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        AbstractCreature creature = effect.getEffected();
        creature.getEffectController().unsetStatus(EffectStatus.Invisible, true);
    }
}
