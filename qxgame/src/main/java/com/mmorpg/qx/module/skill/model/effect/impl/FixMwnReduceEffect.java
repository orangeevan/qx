package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author wang ke
 * @description: 当前卡包扣除指定魔物娘
 * @since 11:29 2020-09-07
 */
public class FixMwnReduceEffect extends AbstractEffectTemplate {

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        AbstractCreature effected = effect.getEffected();
        AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(effected);
        List<Long> targetIds = effect.getEffectTarget().getTargetIds();
        if (CollectionUtils.isEmpty(targetIds)) {
            return false;
        }
        targetIds.stream().forEach(itemId -> trainer.getUseCardStorage().reduceMwn(itemId));
        return true;
    }
}
