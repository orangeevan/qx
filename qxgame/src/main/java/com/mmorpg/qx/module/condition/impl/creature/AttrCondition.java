package com.mmorpg.qx.module.condition.impl.creature;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.condition.AbstractCondition;
import com.mmorpg.qx.module.condition.AbstractCreatureCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import org.springframework.util.CollectionUtils;
import java.util.List;

/**
 * @author wang ke
 * @description: 属性条件
 * @since 16:35 2020-08-24
 */
public class AttrCondition extends AbstractCreatureCondition<Object> {

    private List<Attr> conditions;

    public List<Attr> getConditions() {
        return conditions;
    }

    public void setConditions(List<Attr> conditions) {
        this.conditions = conditions;
    }

    @Override
    protected void init() {
        super.init();
        conditions = JsonUtils.string2List(getParams(), Attr.class);
    }

    @Override
    public Result verify(AbstractCreature creature, Object object1, Object amount) {
        if(!CollectionUtils.isEmpty(conditions)){
            long count = conditions.stream().filter(attr -> creature.getAttrController().getCurrentAttr(attr.getType()) < attr.getValue()).count();
            if(count > 0){
                return Result.FAILURE;
            }
        }
        return Result.SUCCESS;
    }
}
