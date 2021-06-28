package com.mmorpg.qx.module.skill.model.effect.impl;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.resource.EffectResource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 封锁技能
 *
 * @author: yuanchengyan
 * @description:
 * @since 14:07 2021/4/12
 */
public class BlockEffect extends AbstractEffectTemplate {
    private Set<Integer> releaseTypes;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        releaseTypes = new HashSet<Integer>(JsonUtils.string2List(resource.getParam(), Integer.class));
        if (releaseTypes == null) {
            releaseTypes = Collections.EMPTY_SET;
        }
    }

    @Override
    public boolean applyEffect(Effect effect) {
        effect.getEffected().getEffectController().setStatus(EffectStatus.Block, false);
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        effect.getEffected().getEffectController().unsetStatus(EffectStatus.Block, false);
    }

    public boolean contain(int t) {
        return releaseTypes.contains(t);
    }
}

