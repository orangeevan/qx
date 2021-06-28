package com.mmorpg.qx.module.mwn.enums;

/**
 * @author wang ke
 * @description:
 * @since 16:39 2020-09-27
 */
public enum MwnQuality {

    R(1),
    SR(2),
    SSR(3),
    UR(4),
    ;
    private int id;

    private MwnQuality(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static MwnQuality valueOf(int id){
        for (MwnQuality value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        return null;
    }
}
