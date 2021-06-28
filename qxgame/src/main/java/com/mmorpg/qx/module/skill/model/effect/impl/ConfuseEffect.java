package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;

/**
 * @author 魅惑
 * @description:
 * @since 13:54 2021-09-07
 */
public class ConfuseEffect extends AbstractEffectTemplate {
    @Override
    public boolean applyEffect(Effect effect) {
        effect.getEffected().getEffectController().setStatus(EffectStatus.Confuse, false);
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        effect.getEffected().getEffectController().unsetStatus(EffectStatus.Confuse, false);
    }
}
