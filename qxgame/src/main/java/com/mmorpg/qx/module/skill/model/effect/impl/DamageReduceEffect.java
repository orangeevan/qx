package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.target.Target;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description: 伤害减免
 * @since 16:37 2020-09-04
 */
public class DamageReduceEffect extends AbstractEffectTemplate {

    private Attr harmReduce;

    @Override
    public void calculate(Effect effect) {
        // 根据公式计算伤害
        super.calculate(effect);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        harmReduce = Attr.valueOf(AttrType.Harm_Reduce_Rate, effect.getValue());
        chooseTargets(effect);
        Target target = effect.getEffectTarget();
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(target, effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            creatures.stream().forEach(creature -> creature.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Buff_Effect, effect.getEffectResourceId()), harmReduce, true));
            return true;
        }
        return false;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        Attr reverseAttr = harmReduce.reverse();
        Set<AbstractCreature> targets = getTargets();
        if (CollectionUtils.isEmpty(targets)) {
            targets.stream().forEach(target -> target.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Buff_Effect, effect.getEffectResourceId()), reverseAttr, true));
        }
    }
}
