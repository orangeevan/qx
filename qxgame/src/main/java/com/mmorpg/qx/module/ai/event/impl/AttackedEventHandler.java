package com.mmorpg.qx.module.ai.event.impl;

import com.mmorpg.qx.module.ai.event.Event;
import com.mmorpg.qx.module.ai.event.IEventHandler;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;

/**
 * 被攻击事件
 *
 * @author wang ke
 * @since 2020年08月03日
 */
public class AttackedEventHandler implements IEventHandler<AbstractCreature> {
    @Override
    public Event getEvent() {
        return Event.ATTACKED;
    }

    @Override
    public void handleEvent(Event event, AbstractCreature owner, AbstractCreature attacker) {
    }
}
