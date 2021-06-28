package com.mmorpg.qx.module.object.controllers.event;

import com.haipaite.common.event.event.IEvent;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;

/**
 * @author wang ke
 * @description:魔物娘死亡事件
 * @since 16:23 2020-08-25
 */
public class MWNDeadEvent implements IEvent {
    /**
     * 被击杀的魔物娘
     */
    private MWNCreature owner;

    /**
     * 击杀者
     */
    private AbstractCreature killer;

    public static MWNDeadEvent valueOf(MWNCreature owner, AbstractCreature killer) {
        MWNDeadEvent mwnDeadEvent = new MWNDeadEvent();
        mwnDeadEvent.owner = owner;
        mwnDeadEvent.killer = killer;
        return mwnDeadEvent;
    }

    public void setOwner(MWNCreature owner) {
        this.owner = owner;
    }

    @Override
    public long getOwner() {
        return owner.getObjectId();
    }

    public AbstractCreature getKiller() {
        return killer;
    }

    public MWNCreature getDeadMWN(){
        return owner;
    }
}
