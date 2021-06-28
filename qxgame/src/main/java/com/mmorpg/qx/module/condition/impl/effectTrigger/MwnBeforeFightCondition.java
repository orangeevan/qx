package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: yuanchengyan
 * @description:
 * @since 14:27 2021/4/20
 */
public class MwnBeforeFightCondition extends AbstractEffectTriggerCondition<Integer> {
    private Param param;

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.MWN_BEFORE_FIGHT;
    }

    @Override
    protected void init() {
        super.init();
        String params = getParams();
        param = JsonUtils.string2Object(params, MwnBeforeFightCondition.Param.class);
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer amount) {
        Result result = super.verify(trigger, triggerType, amount);
        if (result.isFailure()) {
            return result;
        }
        MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(trigger);
        int supportMwn = param.supportMwn;
        if (supportMwn == -1 && mwnCreature.getSupporter() == 0) {
            return Result.FAILURE;
        }
        if (supportMwn > 0) {
            if (mwnCreature.getSupporter() == 0) {
                return Result.FAILURE;
            }
            MWNCreature mwn = trigger.getMaster().getMwn(mwnCreature.getSupporter());
            return mwn.getObjectKey() == supportMwn ? Result.SUCCESS : Result.FAILURE;
        }
        return Result.SUCCESS;

    }

    public static class Param {
        private int supportMwn;

        public Param() {
        }

        public int getSupportMwn() {
            return supportMwn;
        }

        public void setSupportMwn(int supportMwn) {
            this.supportMwn = supportMwn;
        }
    }
}
