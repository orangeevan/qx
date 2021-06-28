package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description:禁止操作某类建筑固定回合
 * @since 14:47 2020-09-10
 */
public class LimitOpBuildEffect extends AbstractEffectTemplate {

    private ObjectType buildType;

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
        if (StringUtils.isNotEmpty(effect.getEffectResource().getParam())) {
            buildType = ObjectType.valueOf(Integer.valueOf(effect.getEffectResource().getParam()));
        }
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature creature : creatures) {
                creature.getEffectController().setStatus(EffectStatus.Limit_Op_Build, false);
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
                creature.getEffectController().unsetStatus(EffectStatus.Limit_Op_Build, false);
            });
        }
    }

    public ObjectType getBuildType() {
        return buildType;
    }
}
