package com.mmorpg.qx.module.skill.event;

import com.haipaite.common.event.event.IEvent;
import com.mmorpg.qx.module.skill.model.Skill;

/**
 * @author: yuanchengyan
 * @description:
 * @since 20:47 2021/4/23
 */
public class UseSkillEvent implements IEvent {
    private Skill skill;

    @Override
    public long getOwner() {
        return skill.getSkillCaster().getObjectId();
    }

    public static UseSkillEvent valueOf(Skill skill) {
        UseSkillEvent event = new UseSkillEvent();
        event.skill = skill;
        return event;
    }

    public Skill getSkill() {
        return skill;
    }
}

