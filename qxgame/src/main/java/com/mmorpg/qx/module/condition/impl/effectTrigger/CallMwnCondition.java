package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;

/**
 * @author: yuanchengyan
 * @description:
 * @since 14:27 2021/4/20
 */
public class CallMwnCondition extends AbstractEffectTriggerCondition<Integer> {
    private Param param;

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.CALL_MWN;
    }

    @Override
    protected void init() {
        super.init();
        String params = getParams();
        param = JsonUtils.string2Object(params, CallMwnCondition.Param.class);
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer amount) {
        Result result = super.verify(trigger, triggerType, amount);
        if (result.isFailure()) {
            return result;
        }
        int mp = trigger.getLifeStats().getCurrentMp();
        return param.type.verify(mp, param.mp);
    }

    public static class Param {
        private int mp;
        private Type type;

        public Param() {
        }

        public int getMp() {
            return mp;
        }

        public void setMp(int mp) {
            this.mp = mp;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            type = type;
        }
    }

    public static enum Type {
        GREAT {
            @Override
            Result verify(int v1, int v2) {
                return v1 > v2 ? Result.SUCCESS : Result.FAILURE;
            }
        },
        LESS {
            @Override
            Result verify(int v1, int v2) {
                return v1 < v2 ? Result.SUCCESS : Result.FAILURE;
            }
        },
        EQUAL {
            @Override
            Result verify(int v1, int v2) {
                return v1 == v2 ? Result.SUCCESS : Result.FAILURE;
            }
        };

        abstract Result verify(int v1, int v2);
    }
}
