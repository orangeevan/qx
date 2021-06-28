package com.mmorpg.qx.module.condition.impl.mwn;

import com.mmorpg.qx.module.condition.AbstractMwnCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;

/**
 * @author wang ke
 * @description: 穿戴装备
 * @since 21:37 2020-09-03
 */
public class WearEquipCondition extends AbstractMwnCondition {

    @Override
    public Result verify(MWNCreature mwn, Object object1, Object amount) {
        if(!mwn.getAttrController().hasAttrEffect(AttrEffectId.valueOf(AttrEffectType.Equip_Effect))){
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }
}
