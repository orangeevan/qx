package com.mmorpg.qx.module.condition.impl.creature;

import com.mmorpg.qx.module.condition.AbstractCreatureCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * @author wang ke
 * @description: 生存条件
 * @since 20:48 2020-09-03
 */
public class InLivingCondition extends AbstractCreatureCondition<Object> {

    @Override
    public Result verify(AbstractCreature creature, Object object1, Object amount) {
        if (creature.isAlreadyDead()) {
            return Result.FAILURE;
        }
        return Result.SUCCESS;
    }
}
