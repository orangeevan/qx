package com.mmorpg.qx.module.condition.impl.creature;

import com.mmorpg.qx.module.condition.AbstractCreatureCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * @author wang ke
 * @description: 生命值变化触发
 * @since 11:08 2020-09-04
 */
public class HpAlterCondition extends AbstractCreatureCondition<Reason> {
    private Reason reason;

    @Override
    protected void init() {
        super.init();
        String params = getParams();
        if (StringUtils.isEmpty(params)) {
            return;
        }
        reason = Reason.valueOf(params);
    }

    @Override
    public Result verify(AbstractCreature param1, Reason param2, Object amount) {
        AbstractCreature creature = param1;
        if (!creature.isAlreadyDead() && creature.getLifeStats().isFullyRestoredHp()) {
            return Result.FAILURE;
        }
        if (Objects.nonNull(param2)) {
            Reason request = param2;
            if (request != reason) {
                return Result.FAILURE;
            }
        }
        return Result.SUCCESS;
    }
}
