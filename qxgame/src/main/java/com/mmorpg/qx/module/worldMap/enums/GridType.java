package com.mmorpg.qx.module.worldMap.enums;

/**
 * @author wang ke
 * @description: 格子类型
 * @since 18:51 2020-07-30
 */
public enum GridType {
    COMMON(1, "普通地块"),
    BUILDING(2, "建筑地块"),
    ;
    private byte type;
    private String desc;

    private GridType(int type, String desc) {
        this.type = (byte) type;
    }

    public static GridType valueOf(final int type) {
        for (GridType gridType : GridType.values()) {
            if (gridType.type - type == 0) {
                return gridType;
            }
        }
        return null;
    }
}
