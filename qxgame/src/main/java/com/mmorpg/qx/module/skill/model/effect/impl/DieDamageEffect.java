package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Set;

/**
 * @author wang ke
 * @description: 死亡后对攻击目标造成伤害
 * @since 11:37 2020-09-27
 */
public class DieDamageEffect extends AbstractEffectTemplate {

    //有目标类型，给攻击者添加buff
    private int newEffectId;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        newEffectId = Integer.valueOf(resource.getParam());
    }

    @Override
    public boolean applyEffect(Effect effect) {
        //buff带有目标类型
        AbstractCreature trigger = effect.getTrigger();
        //先给触发者造成伤害
        if (Objects.nonNull(trigger) && effect.getValue() > 0) {
            trigger.getLifeStats().reduceHp(effect.getValue(), effect.getEffected(), 0, effect.getEffectResourceId());
        }
        if (newEffectId > 0) {
            chooseTargets(effect);
            Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
            if (!CollectionUtils.isEmpty(creatures)) {
                EffectResource effectResource = SkillManager.getInstance().getEffectResource(newEffectId);
                creatures.stream().forEach(creature -> {
                    Effect newEffect = new Effect(effect.getEffected(), creature, effectResource.getEffectType().create(), 0, newEffectId, Target.valueOf(creature.getGridId(), creature.getObjectId()));
                    newEffect.addToEffectedController();
                });
                return true;
            }
        }
        return false;
    }
}
