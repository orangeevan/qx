package com.mmorpg.qx.module.condition.impl.skill;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.condition.AbstractCondition;
import com.mmorpg.qx.module.condition.AbstractSkillCondition;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author wang ke
 * @description:回合条件
 * @since 20:32 2020-09-09
 */
public class SkillRoundCondition extends AbstractSkillCondition {

    List<Integer> rounds;

    @Override
    protected void init() {
        super.init();
        if (StringUtils.isEmpty(getParams())) {
            return;
        }
        rounds = JsonUtils.string2List(getParams(), Integer.class);
    }

    @Override
    public Result verify(Skill skill, Effect effect, Integer amount) {
        Room room = skill.getSkillCaster().getMaster().getRoom();
        if (rounds != null && rounds.contains(room.getRound())) {
            return Result.SUCCESS;
        }
        return Result.FAILURE;
    }
}
