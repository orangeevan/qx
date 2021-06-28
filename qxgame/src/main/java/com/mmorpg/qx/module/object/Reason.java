package com.mmorpg.qx.module.object;

/**
 * @author wang ke
 * @description: 影响原因，比如掉血，由于魔物娘影响
 * @since 11:19 2020-09-04
 */
public enum Reason {
    //每回合自动加蓝
    Round_Add_Mp,
    //卡包没牌
    No_Card,
    //魔物娘投骰子
    Mwn_Throw_Dice,
    //消耗
    Consume,
    //魔物娘战斗
    Mwn_Fight,
    //建筑
    Building,
    //魔物娘技能效果
    Mwn_Skill_Effect,
    //驯养师技能
    Trainer_Skill_Effect,
    //属性变化
    Attr_Change,
    //魔物娘格子
    Self_Mwn_Position,
    //玩家操作
    Player_Op,
    //回合抽卡
    Round_Extract_Card,
    //召唤魔物娘
    Call_Mwn,
    //进入战斗初始化
    Room_Init,
    //魔物娘替换
    Mwn_Replace,
    //魔物娘战斗胜利
    Mwn_Fight_Win,
    //魔物娘战斗失败
    Mwn_Fight_Fail,
    //爆牌
    Explode_Card,
    //主动技能-操作卡牌
    Operate_Card_Skill,
    //主动技能-置入牌库
    Put_Into_Source_Skill,
    //主动技能-吸血
    Suck_Blood_Skill,
    //主动技能-属性变动
    Change_Attr_Skill,
    //施咒
    Curse,
    ;

}
