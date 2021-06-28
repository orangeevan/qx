package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.model.DicePoint;

import java.util.List;
import java.util.Objects;

/**
 * @author wang ke
 * @description:骰子点数触发
 * @since 19:58 2020-11-03
 */
public class DicePointCondition extends AbstractEffectTriggerCondition<Integer> {

    private List<Integer> points;

    @Override
    protected void init() {
        super.init();
        points = JsonUtils.string2List(getParams(), Integer.class);
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.Dice_Point;
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer amount) {
        Result verify = super.verify(trigger, triggerType, amount);
        if (verify.isSuccess()) {
            AbstractTrainerCreature master = trigger.getMaster();
            DicePoint dicePoint = master.getDicePoint();
            if (Objects.isNull(dicePoint) || !points.contains(dicePoint.calcPoints())) {
                return Result.FAILURE;
            }
        }
        return verify;
    }
}
