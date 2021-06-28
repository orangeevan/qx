package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * 标识性BUFF效果.用于标识该玩家身上有哪些buff而已.
 *
 * @author wang ke
 * @since v1.0 2018年3月3日
 */
public class MarkBuffEffect extends AbstractEffectTemplate {

    private EffectStatus effectStatus;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        int value = resource.getValue();
        Optional<EffectStatus> first = Arrays.stream(EffectStatus.values()).filter(type -> type.getId() == value).findFirst();
        if (first.isPresent()) {
            effectStatus = first.get();
        }
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature creature : creatures) {
                creature.getEffectController().setStatus(effectStatus, false);
            }
            return true;
        }
        return false;
    }

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
    }

}