package com.mmorpg.qx.module.skill.model.effect.impl;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author wang ke
 * @description: 改变骰子点数效果
 * @since 17:19 2020-10-26
 */
public class DicePointsChangeEffect extends AbstractEffectTemplate {
    private int[] dicePoints;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
//        String[] split = resource.getParam().split(",");
//        dicePoints = new int[split.length];
//        for (int i = 0; i < split.length; i++) {
//            dicePoints[i] = Integer.valueOf(split[i]);
//        }
        List<Integer> pl = JsonUtils.string2List(resource.getParam(), Integer.class);
        dicePoints = pl.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (CollectionUtils.isEmpty(creatures)) {
            return false;
        }
        for (AbstractCreature creature : creatures) {
            AbstractTrainerCreature master = creature.getMaster();
            if (master.getRoom() == null || master.getRoom().getCurrentTurn() != master) {
                getTargets().remove(creature);
                return false;
            }
            master.getEffectController().setStatus(EffectStatus.Dice_Points_Change, false);
        }
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        Set<AbstractCreature> targets = getTargets();
        if (!CollectionUtils.isEmpty(targets)) {
            targets.stream().forEach(target -> target.getEffectController().unsetStatus(EffectStatus.Dice_Points_Change, false));
        }
    }

    public int[] getDicePoints() {
        return dicePoints;
    }
}
