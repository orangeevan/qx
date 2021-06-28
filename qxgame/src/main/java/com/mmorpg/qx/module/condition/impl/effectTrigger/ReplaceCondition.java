package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * @author: yuanchengyan
 * @description:
 * @since 17:00 2021/4/20
 */
public class ReplaceCondition extends AbstractEffectTriggerCondition {


    @Override
    public TriggerType getTriggerType() {
        return TriggerType.REPLACE;
    }

    @Override
    public void init(ConditionResource resource) {
        super.init();

    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Object obj) {
       return super.verify(trigger, triggerType, obj);

    }

}

