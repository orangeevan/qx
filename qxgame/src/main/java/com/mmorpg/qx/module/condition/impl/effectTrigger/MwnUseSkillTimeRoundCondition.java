package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;

import java.util.Objects;

/**
 * @author: yuanchengyan
 * @description:
 * @since 14:27 2021/4/20
 */
public class MwnUseSkillTimeRoundCondition extends AbstractEffectTriggerCondition {
    private Param param;

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.MWN_USE_SKILL_TIME_ROUND;
    }

    @Override
    protected void init() {
        super.init();
        String params = getParams();
        param = JsonUtils.string2Object(params, MwnUseSkillTimeRoundCondition.Param.class);
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Object amount) {
        Result result = super.verify(trigger, triggerType, amount);
        if (result.isFailure()) {
            return result;
        }
        int time = trigger.getMaster().getRoom().getUseSkillTimeRound(trigger.getObjectId());
        return param.type.verify(time, param.time);

    }

    public static class Param {
        private int time;
        private Type type;

        public Param() {
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
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
