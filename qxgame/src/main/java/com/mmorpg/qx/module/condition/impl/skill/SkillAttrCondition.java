package com.mmorpg.qx.module.condition.impl.skill;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.condition.AbstractSkillCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author wang ke
 * @description:技能施法需要满足属性点
 * @since 14:55 2020-10-30
 */
public class SkillAttrCondition extends AbstractSkillCondition {
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
    public Result verify(Skill skill, Effect effect, Integer amount) {
        if(!CollectionUtils.isEmpty(conditions)){
            AbstractCreature creature = skill.getSkillCaster();
            long count = conditions.stream().filter(attr -> creature.getAttrController().getCurrentAttr(attr.getType()) < attr.getValue()).count();
            if(count > 0){
                return Result.FAILURE;
            }
        }
        return Result.SUCCESS;
    }
}
