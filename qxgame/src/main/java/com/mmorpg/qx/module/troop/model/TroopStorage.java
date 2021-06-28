package com.mmorpg.qx.module.troop.model;

import com.mmorpg.qx.module.player.model.Player;

import java.util.List;

/**
 * @author zhang peng
 * @description 编队容器
 * @since 14:29 2021/4/8
 */
public class TroopStorage {

    private transient Player player;

    private List<Troop> troops;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Troop> getTroops() {
        return troops;
    }

    public void setTroops(List<Troop> troops) {
        this.troops = troops;
    }
}
