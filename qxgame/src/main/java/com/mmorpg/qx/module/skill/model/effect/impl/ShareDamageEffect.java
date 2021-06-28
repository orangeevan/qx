package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author wang ke
 * @description: 分担魔物娘伤害效果，援护两侧队友魔物娘，分担其受到伤害
 * @since 13:54 2020-09-07
 */
public class ShareDamageEffect extends AbstractEffectTemplate {


    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        //施法者添加分担伤害效果
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature effected : creatures) {
                if (!RelationshipUtils.isMWN(effected)) {
                    return false;
                }
                MWNCreature effectedMWN = RelationshipUtils.toMWNCreature(effected);
                //援护周围队友，分担伤害
                List<MWNCreature> mwnAround = effected.getWorldMapInstance().getMWNAroundGrid(effected.getPosition().getGridId(), false);
                if (CollectionUtils.isEmpty(mwnAround)) {
                    return false;
                }
                List<MWNCreature> assistMWN = new ArrayList<>();
                mwnAround.stream().forEach(mwn -> {
                    if (RelationshipUtils.judgeRelationship(mwn, effectedMWN, RelationshipUtils.Relationships.FRIEND_MWN_MWN)) {
                        assistMWN.add(mwn);
                        if (mwn.getAssistedMWN() == null) {
                            List<MWNCreature> assistedMWN = new ArrayList<>();
                            mwn.setAssistedMWN(assistedMWN);
                        }
                        mwn.getAssistedMWN().add(effectedMWN);
                    }
                });
                //原先有被援护，先移除
                if (!CollectionUtils.isEmpty(effectedMWN.getBeAssistedMWN())) {
                    effectedMWN.getBeAssistedMWN().stream().forEach(beAssisted -> {
                        if (beAssisted.getAssistedMWN() != null && beAssisted.getAssistedMWN().contains(effectedMWN)) {
                            beAssisted.getAssistedMWN().remove(effectedMWN);
                        }
                    });
                }
                effectedMWN.setBeAssistedMWN(assistMWN);
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
                if (!RelationshipUtils.isMWN(creature)) {
                    return;
                }
                MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(creature);
                mwnCreature.getBeAssistedMWN().stream().forEach(beAssisted -> beAssisted.getAssistedMWN().remove(mwnCreature));
                mwnCreature.setBeAssistedMWN(null);
            });
        }
    }
}
