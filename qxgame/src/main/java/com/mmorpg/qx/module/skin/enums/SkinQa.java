package com.mmorpg.qx.module.skin.enums;

import java.util.Arrays;

/**
 * @author wang ke
 * @description: 皮肤品质
 * @since 19:12 2020-09-27
 */
public enum SkinQa {
    Low(1),
    Middle(2),
    High(3),
    ;

    private int id;

    private SkinQa(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static SkinQa valueOf(int id){
        return Arrays.stream(values()).filter(type -> type.id == id).findFirst().get();
    }
}
