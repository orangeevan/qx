package com.mmorpg.qx.module.condition.impl.creature;

import com.mmorpg.qx.module.condition.AbstractCondition;
import com.mmorpg.qx.module.condition.AbstractCreatureCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * @author wang ke
 * @description: 与释放者同格子
 * @since 11:48 2020-09-04
 */
public class InFixPositionCondition extends AbstractCreatureCondition<AbstractCreature> {

    @Override
    public Result verify(AbstractCreature creatureA, AbstractCreature object, Object amount) {
        AbstractCreature creatureB = object;
        if (creatureA.getPosition() == creatureB.getPosition()) {
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }
}
