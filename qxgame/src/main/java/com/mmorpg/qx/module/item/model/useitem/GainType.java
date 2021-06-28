package com.mmorpg.qx.module.item.model.useitem;

/**
 * @author zhang peng
 * @description: 使用道具效果-获得类型
 * @since 15:23 2021/3/4
 */
public enum GainType {

    /** 无 **/
    NONE(0),
    /** 货币 **/
    CURRENCY(2),
    /** 积分 */
    INTEGRAL(3),
    /** 礼包 */
    GIFT(4);

    private final int type;

    GainType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static GainType valueOf(int type) {
        for (GainType temp : GainType.values()) {
            if (temp.getType() == type) {
                return temp;
            }
        }
        return null;
    }

}
