package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import org.apache.commons.lang.StringUtils;

/**
 * @author: yuanchengyan
 * @description:
 * @since 14:27 2021/4/20
 */
public class CallMwnIndexCondition extends AbstractEffectTriggerCondition<Integer> {

    private PositionType positionType = PositionType.RANDOM;

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.CALL_MWN_INDEX;
    }


    @Override
    protected void init() {
        super.init();
        if (StringUtils.isNotBlank(getParams())) {
            positionType = JsonUtils.string2Object(getParams(), PositionType.class);
        }
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer index) {
        if (super.verify(trigger, triggerType, index).isFailure()) {
            return Result.FAILURE;
        }
        if (positionType == PositionType.RANDOM) {
            return Result.SUCCESS;
        }
        int size = trigger.getMaster().getUseCardStorage().getSize();
        if (size <= index && positionType == PositionType.RIGHT) {
            return Result.SUCCESS;
        }
        if (index == 0 && positionType == PositionType.LEFT) {
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }

    private enum PositionType {
        RANDOM,
        LEFT,
        RIGHT,
        ;

        PositionType() {
        }
    }
}

