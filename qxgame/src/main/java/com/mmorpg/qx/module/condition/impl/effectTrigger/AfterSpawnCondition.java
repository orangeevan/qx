package com.mmorpg.qx.module.condition.impl.effectTrigger;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.condition.AbstractEffectTriggerCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author wang ke
 * @description: 新召唤魔物娘
 * @since 19:07 2020-11-05
 */
public class AfterSpawnCondition extends AbstractEffectTriggerCondition<Integer> {
    private List<AttrType> attrTyps;

    @Override
    protected void init() {
        super.init();
        if (StringUtils.isNotBlank(getParams())) {
            attrTyps = JsonUtils.string2List(getParams(), AttrType.class);
        }
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.SPAWN;
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer amount) {
        if (RelationshipUtils.isMWN(trigger)) {
            MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(trigger);
            if (!CollectionUtils.isEmpty(attrTyps)) {
                for (AttrType attrTyp : attrTyps) {
                    if (!mwnCreature.hasAttr(attrTyp)) {
                        return Result.FAILURE;
                    }
                }
            }
        }
        return super.verify(trigger, triggerType, amount);
    }
}
