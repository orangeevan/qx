package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;

import java.util.Objects;

/**
 * @author wang ke
 * @description: 获得具体数值金币效果
 * @since 15:15 2020/12/16
 */
public class AddGoldEffect extends AbstractEffectTemplate {

    @Override
    public boolean applyEffect(Effect effect) {
        AbstractTrainerCreature master = effect.getEffected().getMaster();
        if (Objects.isNull(master)) {
            return false;
        }
        master.getLifeStats().increaseGold(effect.getEffectResource().getValue(), Reason.Trainer_Skill_Effect, false);
        return true;
    }
}
