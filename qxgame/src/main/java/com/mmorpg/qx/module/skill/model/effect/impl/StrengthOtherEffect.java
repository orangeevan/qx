package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.resource.EffectResource;

import java.util.Objects;

/**
 * @author wang ke
 * @description: 加强别的buff效果
 * @since 11:35 2020-10-29
 */
public class StrengthOtherEffect extends AbstractEffectTemplate {

    private int strengthEffect;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        strengthEffect = Integer.valueOf(resource.getParam());
    }

    @Override
    public boolean applyEffect(Effect effect) {
        //检查拥有者身上是否有对应buff
        Effect strenEffect = effect.getEffected().getEffectController().getEffect(strengthEffect);
        if (Objects.isNull(strenEffect)) {
            return false;
        }
        int value = strenEffect.getValue() + effect.getValue();
        strenEffect.setValue(value);
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        Effect strenEffect = effect.getEffected().getEffectController().getEffect(strengthEffect);
        if (Objects.isNull(strenEffect)) {
            return;
        }
        int value = effect.getValue() - strenEffect.getValue();
        strenEffect.setValue(value);
    }
}
