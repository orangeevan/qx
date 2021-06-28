package com.mmorpg.qx.module.roundFight.event;

import com.haipaite.common.event.event.IEvent;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.player.model.Player;

/**
 * @author: yuanchengyan
 * @description:
 * @since 11:28 2021/4/20
 */
public class PlayerCallMwnEvent implements IEvent {
    private int index;
    private AbstractCreature creature;

    @Override
    public long getOwner() {
        return creature.getObjectId();
    }

    public static PlayerCallMwnEvent valueOf(AbstractCreature creature, int index) {
         PlayerCallMwnEvent event = new PlayerCallMwnEvent();
        event.index = index;
        event.creature = creature;
        return event;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public AbstractCreature getCreature() {
        return creature;
    }

    public void setCreature(AbstractCreature creature) {
        this.creature = creature;
    }
}

