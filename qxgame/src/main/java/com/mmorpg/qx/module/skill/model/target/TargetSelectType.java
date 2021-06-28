package com.mmorpg.qx.module.skill.model.target;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wang ke
 * @description: 目标选择方式
 * @since 10:44 2020-11-13
 */
public enum TargetSelectType {
    Random(0),
    Fix(1),
    ;
    private int id;

    TargetSelectType(int id) {
        this.id = id;
    }

    public static TargetSelectType valueOf(int id) {
        return Arrays.stream(TargetSelectType.values()).filter(type -> type.id == id).findFirst().orElse(null);
    }
}
