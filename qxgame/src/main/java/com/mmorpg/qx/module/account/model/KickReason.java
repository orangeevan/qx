package com.mmorpg.qx.module.account.model;

/**
 * 踢人下线原因
 *
 * @author wang ke
 * @since v1.0 2018年3月8日
 */
public enum KickReason {
    /**
     * 重复登陆
     */
    REPEATED_LOGIN(1),
    /**
     * 玩家长时间没有心跳
     */
    HEARTBEAT_DELAY(2),
    /**
     * GM踢人
     */
    GM_KICKOFF(3),

    /**
     * 退出游戏
     */
    QUIT_GAME(4),
    ;
    private int value;

    private KickReason(int value) {
        this.setValue(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
