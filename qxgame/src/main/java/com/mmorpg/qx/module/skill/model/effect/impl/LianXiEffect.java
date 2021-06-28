package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;

/**
 * 连袭
 *
 * @author: yuanchengyan
 * @description:
 * @since 14:07 2021/4/12
 */
public class LianXiEffect extends AbstractEffectTemplate {
    @Override
    public boolean applyEffect(Effect effect) {
        effect.getEffected().getEffectController().setStatus(EffectStatus.Lianxi, true);
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        effect.getEffected().getEffectController().unsetStatus(EffectStatus.Lianxi, true);
    }
}

