package com.mmorpg.qx.module.mwn.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wang ke
 * @description: 魔物娘类型
 * @since 14:14 2020-09-28
 */
public enum MwnType {

    Random(1),
    Special(2),
    ;
    private int id;

    private MwnType(int id) {
        this.id = id;
    }

    public static MwnType valueOf(int id){
        Optional<MwnType> any = Arrays.stream(values()).filter(mwnType -> mwnType.id == id).findAny();
        if (any.isPresent()) {
            return any.get();
        }
        throw new IllegalArgumentException("魔物娘类型不存在: "+id);
    }
}
