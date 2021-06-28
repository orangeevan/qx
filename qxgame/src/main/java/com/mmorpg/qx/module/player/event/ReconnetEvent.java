package com.mmorpg.qx.module.player.event;

import com.haipaite.common.event.event.IEvent;
import com.mmorpg.qx.module.player.model.Player;

/**
 * 玩家玩家重连事件
 *
 * @author chy.yuan
 * @since v1.0 2019年2月7日
 */
public class ReconnetEvent implements IEvent {
    private Player player;

    public static ReconnetEvent valueOf(Player player) {
        ReconnetEvent loginEvent = new ReconnetEvent();
        loginEvent.player = player;
        return loginEvent;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public long getOwner() {
        return player.getObjectId();
    }
}
