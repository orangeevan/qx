package com.mmorpg.qx.module.roundFight.event;

import com.haipaite.common.event.event.IEvent;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.roundFight.model.Room;

/**
 * @author: yuanchengyan
 * @description:
 * @since 11:28 2021/4/20
 */
public class RoundStageChangeEvent implements IEvent {
    private Room room;

    @Override
    public long getOwner() {
        return room.getRoomId();
    }

    public static RoundStageChangeEvent valueOf(Room room) {
        RoundStageChangeEvent event = new RoundStageChangeEvent();
        event.room = room;
        return event;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}

