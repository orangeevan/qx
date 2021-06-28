package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;

/**
 * @author wang ke
 * @description: 魔物娘死亡时返回玩家手牌，同时下次召唤所需要的魔法值-1（魔法值-1的效果可叠加）
 * @since 10:43 2020-10-29
 */
public class MwnDeadToCardEffect extends AbstractEffectTemplate {
    @Override
    public boolean applyEffect(Effect effect) {
        AbstractCreature trigger = effect.getTrigger();
        if (!RelationshipUtils.isMWN(trigger)) {
            return false;
        }
        MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(trigger);
        AbstractTrainerCreature master = mwnCreature.getMaster();
        if(master.getUseCardStorage().isFull()){
            return false;
        }
        MoWuNiang moWuNiang = mwnCreature.getMwn();
        moWuNiang.setHasThrowDice(false);
        master.getUseCardStorage().addMwn(moWuNiang);
        return true;
    }
}
