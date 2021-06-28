package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description: 提升操作费用类魔法
 * @since 17:25 2020-09-07
 */
public class OperateCostEffect extends AbstractEffectTemplate {

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        //施法者添加状态
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature creature : creatures) {
                creature.getEffectController().setStatus(EffectStatus.Operate_Cost, true);
                String info = String.format("生物 【%s】 添加消耗加倍效果", creature.getObjectId() + "|" + creature.getName());
                System.err.println(info);
            }
            return true;
        }
        return false;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        Set<AbstractCreature> creatures = getTargets();
        if (!CollectionUtils.isEmpty(creatures)) {
            creatures.stream().forEach(creature -> {
                creature.getEffectController().unsetStatus(EffectStatus.Operate_Cost, true);
                String info = String.format("生物 【%s】 移除消耗加倍效果", creature.getObjectId() + "|" + creature.getName());
                System.err.println(info);
            });
        }
    }
}
