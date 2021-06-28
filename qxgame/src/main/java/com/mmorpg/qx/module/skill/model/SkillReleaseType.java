package com.mmorpg.qx.module.skill.model;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wang ke
 * @description: 技能释放类型
 * @since 18:52 2020-09-28
 */
public enum SkillReleaseType {
    Trainer_Active_Skill(1, "驯养师主动魔法"),
    Trainer_Genius_Skill(2, "驯养师天赋技能"),
    Mwn_Job_Skill(3, "魔物娘职业技能"),
    Mwn_Genius_Skill(4, "魔物娘天赋技能"),
    Mwn_Dice_Skill(5, "魔物娘投骰子技能"),
    Boss_Skill(6, "BOSS技能"),
    Passive_Skill(101, "被动(套BUFF用)"),
    ;
    private int id;
    private String desc;

    private SkillReleaseType(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static SkillReleaseType valueOf(int id) {
        Optional<SkillReleaseType> first = Arrays.stream(values()).filter(type -> type.id == id).findFirst();
        return first.isPresent() ? first.get() : null;
    }

}
