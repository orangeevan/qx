package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 冲锋效果
 *
 * @author: yuanchengyan
 * @description:
 * @since 14:07 2021/4/12
 */
public class ChargeEffect extends AbstractEffectTemplate {

    @Override
    public boolean applyEffect(Effect effect) {
        effect.getEffected().getEffectController().setStatus(EffectStatus.Charge, false);
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        effect.getEffected().getEffectController().unsetStatus(EffectStatus.Charge, false);
    }
}

