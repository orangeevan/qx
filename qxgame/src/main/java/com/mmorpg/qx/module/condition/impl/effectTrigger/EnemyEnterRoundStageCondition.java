package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;

/**
 * @author: yuanchengyan
 * @description:
 * @since 14:27 2021/4/20
 */
public class EnemyEnterRoundStageCondition extends AbstractEffectTriggerCondition<Integer> {
    private RoundStage roundStage;

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.ENEMY_ENTER_ROUND_STAGE;
    }

    @Override
    public void init() {
        super.init();
        String params = getParams();
        params.replaceAll("\"","");
        params="\""+params+"\"";
        roundStage = JsonUtils.string2Object(params, RoundStage.class);
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer amount) {
        Result result = super.verify(trigger, triggerType, amount);
        if (result.isFailure()) {
            return result;
        }
        boolean same = trigger.getMaster().getRoom().getRoundStage().equals(roundStage);
        return same ? Result.SUCCESS : Result.FAILURE;
    }


}
