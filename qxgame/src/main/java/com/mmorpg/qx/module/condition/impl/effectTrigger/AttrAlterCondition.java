package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author wang ke
 * @description: 属性变化触发
 * @since 14:50 2020-11-03
 */
public class AttrAlterCondition extends AbstractEffectTriggerCondition<List<Attr>> {
    private AttrType type;

    @Override
    protected void init() {
        super.init();
        if (StringUtils.isNotBlank(getParams())) {
            type = AttrType.valueOf(getParams());
        }
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, List<Attr> attrs) {
        if (super.verify(trigger, triggerType, attrs).isFailure()) {
            return Result.FAILURE;
        }
        Result result = Result.FAILURE;
        for (Attr attr : attrs) {
            if (type != null) {
                if (attr.getType() == type) {
                    result = Result.SUCCESS;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.ATTR;
    }
}
