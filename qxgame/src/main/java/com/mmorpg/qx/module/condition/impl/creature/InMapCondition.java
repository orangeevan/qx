package com.mmorpg.qx.module.condition.impl.creature;

import com.mmorpg.qx.module.condition.AbstractCreatureCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * @author wang ke
 * @description: 对象存在于场上
 * @since 20:37 2020-09-03
 */
public class InMapCondition extends AbstractCreatureCondition<Object> {
    @Override
    public Result verify(AbstractCreature creature, Object object1, Object amount) {
        if (creature.getWorldMapInstance() != null && creature.getPosition() != null) {
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }
}
