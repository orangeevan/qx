package com.mmorpg.qx.module.equipment.enums;

/**
 * @author wang ke
 * @description: 装备类型
 * @since 10:41 2020-11-02
 */
public enum EquipType {
    Head(1, "头盔"),
    Jewelry(2, "饰品"),
    Shoe(3, "鞋子"),
    Defend(4, "护甲"),
    Shield(5, "护盾"),
    Weapon(6, "武器"),
    ;
    private int id;
    private String desc;

    private EquipType(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static EquipType valueOf(int id) {
        for (EquipType value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        throw new IllegalArgumentException("不存在装备类型： " + id);
    }
}
