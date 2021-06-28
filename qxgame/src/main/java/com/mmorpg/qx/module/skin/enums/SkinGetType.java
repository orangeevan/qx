package com.mmorpg.qx.module.skin.enums;

import java.util.Arrays;

/**
 * @author wang ke
 * @description: 皮肤获取方式
 * @since 19:19 2020-09-27
 */
public enum SkinGetType {
    Original(1),
    Shop(2),
    Break_Through(3),
    ;

    private int id;

    SkinGetType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static SkinGetType valueOf(int id){
        return Arrays.stream(values()).filter(type -> type.id == id).findFirst().get();
    }
}
