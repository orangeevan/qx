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
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description: 伤害加成
 * @since 16:12 2020-09-04
 */
public class DamageAddEffect extends AbstractEffectTemplate {
    private Attr harmAdd;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
    }

    @Override
    public void calculate(Effect effect) {
        // 根据公式计算伤害
        super.calculate(effect);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        if (effect.getEffected().isAlreadyDead()) {
            return false;
        }
        chooseTargets(effect);
        harmAdd = Attr.valueOf(AttrType.Harm_Add_Rate, effect.getValue());
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            creatures.stream().forEach(creature -> creature.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Buff_Effect, effect.getEffectResourceId()), harmAdd, true));
            return true;
        }
        return false;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        Set<AbstractCreature> targets = getTargets();
        if (!CollectionUtils.isEmpty(targets)) {
            targets.stream().forEach(target -> target.getAttrController().removeModifiers(AttrEffectId.valueOf(AttrEffectType.Buff_Effect, effect.getEffectResourceId())));
        }
    }
}
