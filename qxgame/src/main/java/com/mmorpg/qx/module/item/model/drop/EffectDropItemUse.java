package com.mmorpg.qx.module.item.model.drop;

import com.mmorpg.qx.module.item.resource.ItemResource;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.DropItemCreature;
import com.mmorpg.qx.module.object.gameobject.enums.DropType;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.resource.EffectResource;

/**
 * @author wang ke
 * @description: 效果类掉落物
 * @since 14:25 2020-10-12
 */
public class EffectDropItemUse extends AbstractDropItemUse {
    @Override
    boolean doUseDropItem(AbstractTrainerCreature trainer, DropItemCreature dropItem) {
        if (dropItem.getDropType() != getDropType()) {
            throw new RuntimeException(String.format("掉落物处理类型错误, 掉落物 [%s] ,掉落类型 [%s]", dropItem.getName() + dropItem.getDropType()));
        }
        ItemResource resource = dropItem.getResource();
        int effectId = Integer.valueOf(resource.getExt());
        EffectResource effectResource = SkillManager.getInstance().getEffectResource(effectId);
        Effect effect = new Effect(trainer, trainer, effectResource.getEffectType().create(), 0, effectId, Target.valueOf(trainer.getGridId(), trainer.getObjectId()));
        //trainer.getEffectController().addEffect(effect);
        effect.addToEffectedController();
        return true;
    }

    @Override
    DropType getDropType() {
        return DropType.Effect;
    }
}
