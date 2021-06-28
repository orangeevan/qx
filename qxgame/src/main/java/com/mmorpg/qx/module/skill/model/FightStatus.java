package com.mmorpg.qx.module.skill.model;

/**
 * @author wang ke
 * @description: 战斗过程中状态
 * @since 15:00 2020-08-27
 */
public enum FightStatus {
    /**
     * 正常攻击
     */
    NORMAL,
    /**
     * 暴击
     */
    CRIT,
    /**
     * 闪避
     */
    MISS,
    /**
     * 减伤
     */
    AVOID_HARM,
    ;
}
