package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.resource.EffectResource;

/**
 * 闪避效果
 *
 * @author: yuanchengyan
 * @description:
 * @since 17:23 2021/4/13
 */
public class MissEffect extends AbstractEffectTemplate {

    @Override
    public void init(EffectResource resource) {
        super.init(resource);

    }

    @Override
    public boolean applyEffect(Effect effect) {

        return true;
    }

}

