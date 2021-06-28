package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * @author: yuanchengyan
 * @description:
 * @since 14:27 2021/4/20
 */
public class RoundCondition extends AbstractEffectTriggerCondition<Integer> {
    private Param param;

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.ROUND;
    }

    @Override
    protected void init() {
        super.init();
        String params = getParams();
        param = JsonUtils.string2Object(params, RoundCondition.Param.class);
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer amount) {
        int round = trigger.getMaster().getRoom().getRound();
        return param.type.verify(round, param.round);
    }

    public static class Param {
        private int round;
        private Type type;

        public Param() {
        }

        public int getRound() {
            return round;
        }

        public void setRound(int round) {
            this.round = round;
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
