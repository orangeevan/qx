package com.mmorpg.qx.module.condition.impl.creature;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.condition.AbstractCondition;
import com.mmorpg.qx.module.condition.AbstractCreatureCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author wang ke
 * @description: 释放对象+效果对象双方满足属性条件触发 [属性类型];[属性类型]
 * @since 10:10 2020-09-04
 */
public class CasterEffectorAttrCondition extends AbstractCreatureCondition<AbstractCreature> {
    /**
     * 释放方属性点
     */
    private List<AttrType> casterAttr;
    /**
     * 承受方属性点
     */
    private List<AttrType> effectedAttr;

    @Override
    protected void init() {
        super.init();
        String[] split = StringUtils.split(getParams(), ";");
        casterAttr = JsonUtils.string2List(split[0], AttrType.class);
        effectedAttr = JsonUtils.string2List(split[1], AttrType.class);
    }

    @Override
    public Result verify(AbstractCreature caster, AbstractCreature object, Object amount) {
        AbstractCreature effected = object;
        Optional<AttrType> casterOp = casterAttr.stream().filter(type -> !caster.getAttrController().hasAttr(type)).findAny();
        if (casterOp.isPresent()) {
            return Result.FAILURE;
        }
        Optional<AttrType> effectedOp = effectedAttr.stream().filter(type -> !effected.getAttrController().hasAttr(type)).findAny();
        if (effectedOp.isPresent()) {
            return Result.FAILURE;
        }
        return Result.SUCCESS;
    }
}
