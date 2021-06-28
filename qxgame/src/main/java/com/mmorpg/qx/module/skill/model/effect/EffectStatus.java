package com.mmorpg.qx.module.skill.model.effect;

/**
 * 效果状态
 *
 * @author wang ke
 * @since v1.0 2018年3月3日
 */
public enum EffectStatus {
    /**
     * 休眠空闲
     */
    Idle(1),
    /**
     * 隐身
     */
    Invisible(2),
    /**
     * 无敌
     */
    God(3),
    /**
     * 定身击晕
     */
    Stun(4),
    /**
     * 禁止投骰子
     */
    Limit_Throw_Dice(5),
    /**
     * 禁止召唤魔物娘
     */
    Limit_Call_Mwn(6),
    /**
     * 溅射效果
     */
    Splash(7),
    /**
     * 操作费用加倍状态
     */
    Operate_Cost(8),
    /**
     * 护盾效果
     */
    Shield(9),
    /**
     * 限制建筑操作
     */
    Limit_Op_Build(10),
    /**
     * 禁止魔法增长
     */
    Limit_Mp_Inc(11),
    /**
     * 魔物娘投骰子后，获得额外在终点两侧空白地放置魔物娘机会
     */
    Arround_Blank_Grid_Set_Mwn(12),
    /**
     * 魔物娘强袭后
     */
    Mwn_Assault(13),
    /**
     * 偷取敌方驯养师金币
     */
    Steal_Gold(14),
    /**
     * 魔物娘添加武力
     */
    Add_Mwn_Trainer_Harm(15),
    /**
     * 改变骰子点数随机范围
     */
    Dice_Points_Change(16),
    /**
     * 额外召唤魔物娘
     */
    Set_Mwn(17),
    /**
     * 改变魔物娘武力值
     */
    Alter_Mwn_Trainer_Harm(18),

    /**
     * 魔法强度降低技能伤害
     */
    Magicstren_Dec_Harm(19),

    /**
     * 魔法强度增加恢复
     */
    Magicstren_Inc_Rec(20),

    /**
     * 减免建筑cd期间多余费用
     */
    Build_Op_Reduce_Cost(21),

    /**
     * 提升金币获取量
     */
    Inc_Gold(22),
    /**
     * 魔物娘投骰子后，获得额外在终点前面空白地放置魔物娘机会
     */
    Beofre_Blank_Grid_Set_Mwn(23),

    /**
     * 魔物娘投骰子后，获得额外在终点后侧空白地放置魔物娘机会
     */
    After_Blank_Grid_Set_Mwn(24),

    /**
     * 禁止使用装备
     */
    Limit_Use_Equip(25),

    /**
     * 禁止释放技能
     */
    Limit_Use_Skill(26),

    /**
     * 魔物娘投骰子
     */
    Mwn_Throw_Dice(27),
    /**
     * 封锁
     */
    Block(28),
    /**
     * 魅惑
     */
    Confuse(29),
    /**
     * 模仿
     */
    Imitate(30),
    /**
     * 瘫痪
     */
    Paralysis(31),
    /**
     * 冲锋
     */
    Charge(33),
    /**
     * 施咒
     */
    Curse(34),
    /**
     * 沉默
     */
    Silence(35),
    /**
     * 挑衅
     */
    Provocation(36),
    /**
     * 连袭
     */
    Lianxi(37),
    ;


    private int id;

    EffectStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}