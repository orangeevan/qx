package com.mmorpg.qx.module.roundFight.event;

import com.haipaite.common.event.event.IEvent;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * @author: yuanchengyan
 * @description:
 * @since 11:28 2021/4/20
 */
public class PlayerMwnReplaceEvent implements IEvent {
    private AbstractCreature onCreature;
    private AbstractCreature offCreature;

    @Override
    public long getOwner() {
        return onCreature.getObjectId();
    }

    public static PlayerMwnReplaceEvent valueOf(AbstractCreature onCreature, AbstractCreature offCreature) {
        PlayerMwnReplaceEvent event = new PlayerMwnReplaceEvent();
        event.onCreature = onCreature;
        event.offCreature = offCreature;
        return event;
    }

    public AbstractCreature getOnCreature() {
        return onCreature;
    }

    public void setOnCreature(AbstractCreature onCreature) {
        this.onCreature = onCreature;
    }

    public AbstractCreature getOffCreature() {
        return offCreature;
    }

    public void setOffCreature(AbstractCreature offCreature) {
        this.offCreature = offCreature;
    }
}

