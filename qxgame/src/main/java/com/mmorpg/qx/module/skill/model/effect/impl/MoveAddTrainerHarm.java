package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description: 移动后至回合结束期间，召唤的魔物娘武力值+1
 * @since 15:11 2020-10-26
 */
public class MoveAddTrainerHarm extends AbstractEffectTemplate {
    private int addHarm;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        addHarm = Integer.valueOf(resource.getParam());
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature creature : creatures) {
                AbstractTrainerCreature master = creature.getMaster();
                if (master.getRoom() == null || master.getRoom().getCurrentTurn() != master || !master.getRoom().isInStage(RoundStage.MOVE)) {
                    getTargets().remove(creature);
                    return false;
                }
                master.getEffectController().setStatus(EffectStatus.Add_Mwn_Trainer_Harm, false);
            }
            return true;
        }
        return false;
    }

    /**
     * 添加魔物娘武力值
     *
     * @return
     */
    public int getAddHarm() {
        return addHarm;
    }
}
