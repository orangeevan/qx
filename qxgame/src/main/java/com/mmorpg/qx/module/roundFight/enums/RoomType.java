package com.mmorpg.qx.module.roundFight.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wang ke
 * @description:房间类型
 * @since 19:16 2020-08-17
 */
public enum RoomType {
    PVP(1, "PVP 1V1"),
    PVE(2, "PVE 1V1"),
    ;
    private byte id;
    private String desc;

    private RoomType(int id, String desc) {
        this.id = (byte) id;
        this.desc = desc;
    }

    public static RoomType valueOf(int id) {
        Optional<RoomType> any = Arrays.stream(RoomType.values()).filter(type -> type.id == id).findAny();
        if (any.isPresent()) {
            return any.get();
        }
        throw new IllegalArgumentException("房间id类型错误: " + id);
    }

    public byte getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }
}
