package com.mmorpg.qx.module.item.model;

import com.mmorpg.qx.module.player.model.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * 背包基础模块
 *
 * @author wang ke
 * @since v1.0 2018年3月6日
 */
public class ItemStorage {

    /** Map<objectId, PackItem> */
    private Map<Long, PackItem> items = new HashMap<>();

    private transient Player owner;

    public Map<Long, PackItem> getItems() {
        return items;
    }

    public void setItems(Map<Long, PackItem> items) {
        this.items = items;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
