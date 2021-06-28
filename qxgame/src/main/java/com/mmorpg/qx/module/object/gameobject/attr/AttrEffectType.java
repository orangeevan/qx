package com.mmorpg.qx.module.object.gameobject.attr;

public enum AttrEffectType {
    /**
     * 基础等级
     */
    Level_Base(1),
    /**
     * 技能效果(被动技能)
     */
    Skill_Effect(2),
    /**
     * 道具效果
     */
    Item_Effect(3),
    /**
     * 技能效果
     */
    Buff_Effect(4),
    /**
     * 装备效果
     */
    Equip_Effect(5),
    /**
     * 回合默认效果
     */
    Round_Effect(6),
    /**
     * 魔物娘属性变化导致驯养师变化
     */
    MWN_ATTR_JOB_ELE_CHANGE(7),
    /**
     * Debug
     */
    DEBUG(8),
    /**
     * 魔物娘流放
     */
    Mwn_Exile(9),

    /**
     * 魔物娘援助
     */
    Mwn_Support(10),

    /**
     * 主动技能-属性变动
     */
    Change_Attr_Skill(11),
    /**
     * 魔物娘援助模仿
     */
    Mwn_Support_Imitate(12),
    ;

    private int value;

    AttrEffectType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
