package com.mmorpg.qx.module.item.enums;

/**
 * @author zhang peng
 * @description: 道具更新reason
 * @since 15:23 2021/3/4
 */
public enum ItemUpdateReason {

    /** 初始化 */
    INIT(0),
    /** 使用道具 */
    USE_ITEM(1),
    /** 获得道具 */
    GAIN_ITEM(2),
    /** 道具过期 */
    ITEM_EXPIRED(3);

    final private int reason;

    ItemUpdateReason(int reason) {
        this.reason = reason;
    }

    public int getReason() {
        return reason;
    }
}
