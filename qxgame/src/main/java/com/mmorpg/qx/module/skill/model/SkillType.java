package com.mmorpg.qx.module.skill.model;

/**
 * @author wang ke
 * @description: 技能类型，分类处理
 * @since 13:50 2020-08-31
 */
public enum SkillType {

    //buff型技能，简单添加buff，没有技能处理过程
    None,
    //伤害型技能
    Harm,
    //复活
    Relive_Mwn,
    //恢复
    Recover,
    //祭坛获取魔物娘
    Random_Mwn,
    //金币
    Gold,
    //击杀目标后召唤魔物娘
    Kill_And_Call,
    //换血
    Exchange_Hp,
    //变身
    Transform,
    //召唤固定魔物娘
    Call_Fixed_Mwn,
    //投骰子前可用，释放后，本回合内移动途径敌方驯养师时获得敌方身上一半金币
    Trainer_Steal_Gold,
    //强袭技能，给己方所有魔物娘添加强袭buff
    Assault_Attack,
    //魔物娘投骰子
    Mwn_Throw_Dice,

    //属性变动
    Change_Attr,
    //卡牌操作
    Operate_Card,
    //魔物娘置入牌库
    Put_Into_Source,
    //吸血
    Suck_Blood,
    //净化效果
    Purify_Effect,
    ;
}
