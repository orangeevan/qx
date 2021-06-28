package com.mmorpg.qx.module.item.model.useitem;

/**
 * @author zhang peng
 * @description
 * @since 14:05 2021/4/27
 */
public enum UseType {

    /** 出售 **/
    SALE(1),
    /** 使用 */
    USE(2);

    private final int type;

    UseType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static UseType valueOf(int type) {
        for (UseType temp : UseType.values()) {
            if (temp.getType() == type) {
                return temp;
            }
        }
        return null;
    }
}
