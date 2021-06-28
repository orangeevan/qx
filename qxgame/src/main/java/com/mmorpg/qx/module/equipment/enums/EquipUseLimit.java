package com.mmorpg.qx.module.equipment.enums;

/**
 * @author wang ke
 * @description:装备使用限制
 * @since 11:43 2020-11-02
 */
public enum EquipUseLimit {
    None(0, "无使用限制"),
    Use_Attack(1, "仅魔物娘进攻时可用"),
    Use_Defend(2, "仅魔物娘防守时可用"),
    ;

    private int id;

    private String desc;

    private EquipUseLimit(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static EquipUseLimit valueOf(int id) {
        for (EquipUseLimit value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        throw new IllegalArgumentException("不存在装备使用类型： " + id);
    }
}
