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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: yuanchengyan
 * @description:
 * @since 14:27 2021/4/20
 */
public class CallMwnGridPosCondition extends AbstractEffectTriggerCondition {
    private Param param;

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.CALL_MWN_GRID_POS;
    }


    @Override
    protected void init() {
        super.init();
        if (StringUtils.isNotBlank(getParams())) {
            param = JsonUtils.string2Object(getParams(), Param.class);
        }
    }

    @Override
    public Result verify(AbstractCreature trigger, TriggerType triggerType, Object obj) {
        int gridId = trigger.getGridId();

        List<MWNCreature> creatures = trigger.getWorldMapInstance().getMWNAroundGrid(gridId, false);
        creatures = creatures.stream().filter(c -> RelationshipUtils.judgeRelationship(trigger, c, RelationshipUtils.Relationships.FRIEND_MWN_MWN)).collect(Collectors.toList());
        long count = creatures.size();

        if (param.getAttrTypes().size() > 0) {
            count = creatures.stream().filter(creature -> {
                List<AttrType> attrTypes = param.getAttrTypes();
                for (AttrType attrType : attrTypes) {
                    if (!creature.hasAttr(attrType)) {
                        return false;
                    }
                }
                return true;
            }).count();
        }
        if (count < param.count) {
            return Result.FAILURE;
        }
        return Result.SUCCESS;

    }

    public static class Param {
        private long count;
        private List<AttrType> attrTypes;

        public Param() {
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public List<AttrType> getAttrTypes() {
            return attrTypes == null ? Collections.EMPTY_LIST : attrTypes;
        }

        public void setAttrTypes(List<AttrType> attrTypes) {
            this.attrTypes = attrTypes;
        }
    }
}

