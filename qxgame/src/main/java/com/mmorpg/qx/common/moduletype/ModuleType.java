package com.mmorpg.qx.common.moduletype;

public enum ModuleType {
    /**
     * 编队
     */
    TROOP(1),
    /**
     * 商店
     */
    SHOP(2),
    /**
     * 任务
     */
    QUEST(3),
    /**
     * 道具
     */
    ITEM(4),

    ;
    private final int id;

    private ModuleType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ModuleType valueOf(int id) {
        for (ModuleType value : ModuleType.values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        throw new IllegalArgumentException("不存在的模块类型: " + id);
    }
}
