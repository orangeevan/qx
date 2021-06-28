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

import javax.management.relation.Relation;
import java.util.List;

/**
 * @author wang ke
 * @description: 指定属性魔物娘死亡
 * @since 18:00 2020/12/23
 */
public class DieMwnFixAttrCondition extends AbstractEffectTriggerCondition<Integer> {
    private List<AttrType> attrTypes;

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.DIE_MWN;
    }

    @Override
    protected void init() {
        super.init();
        String params = getParams();
        attrTypes = JsonUtils.string2List(params, AttrType.class);
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Integer amount) {
        MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(trigger);
        if(!CollectionUtils.isEmpty(attrTypes)){
            for (AttrType attrType : attrTypes) {
                if (!mwnCreature.hasAttr(attrType)) {
                    return Result.FAILURE;
                }
            }
        }
        return super.verify(trigger, triggerType, amount);
    }
}
