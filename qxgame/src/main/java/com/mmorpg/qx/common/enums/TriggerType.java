package com.mmorpg.qx.common.enums;

public enum TriggerType {
    // 移动
    MOVE,
    // 使用技能
    SKILL_USE,
    // 被攻击
    ATTACKED,
    //新回合开始
    ROUND_BEGIN,
    // 死亡
    DIE,
    //属性变化
    ATTR,
    // 出现在地图上
    SPAWN,
    // 看见生物
    SEE,
    //魔物娘之间战斗死亡
    DIE_MWN_MWN,
    //技能效果死亡
    DIE_BY_SKILL_EFFECT,
    //驯养师主动魔法
    Trainer_Active_SKill,
    //路过格子
    Move_Pass,
    //BuffB目标对象身上的某个buffA被触发后，BuffB才可被触发
    Effect_Trigger,
    //骰子点数
    Dice_Point,
    //穿戴装备后触发
    WEAR_EQUIP,
    //回合结束触发
    RoundStage_End,
    //魔物娘死亡
    DIE_MWN,
    //召唤魔物娘(手牌位置)
    CALL_MWN_INDEX,
    //场上位置
    CALL_MWN_GRID_POS,
    //感应
    CALL_MWN_SENSE,
    //换阵
    REPLACE,
    //魔物娘对战前
    MWN_BEFORE_FIGHT,
    //魔物娘对战前作为攻击方
    MWN_BEFORE_FIGHT_ATTACKER,
    //魔物娘对战前作为防守方
    MWN_BEFORE_FIGHT_DEFENDER,
    //魔物娘对战前敌方
    MWN_BEFORE_FIGHT_ENEMY,
    //魔物娘战斗胜利
    MWN_FIGHT_WIN,
    //魔物娘战斗失败
    MWN_FIGHT_LOSE,
    //回合内使用技能
    MWN_USE_SKILL_TIME_ROUND,
    //召唤魔物娘
    CALL_MWN,
    //回合情况
    ROUND,
    //敌方进入指定阶段
    ENEMY_ENTER_ROUND_STAGE,
    //指定阶段
    ENTER_ROUND_STAGE,
    //鼓励
    ENCOURAGE,
    //连袭
    LIANX_XI,
    //回合内使用技能次数
    USE_SKILL_ROUND,
    //回合内消耗魔法值
    CONSUME_MP_ROUND
    ;
}
