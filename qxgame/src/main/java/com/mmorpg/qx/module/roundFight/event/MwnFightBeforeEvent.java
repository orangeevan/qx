package com.mmorpg.qx.module.roundFight.event;

import com.haipaite.common.event.event.IEvent;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * @author: yuanchengyan
 * @description:
 * @since 11:28 2021/4/20
 */
public class MwnFightBeforeEvent implements IEvent {
    private AbstractCreature attacker;
    private AbstractCreature defender;

    @Override
    public long getOwner() {
        return attacker.getObjectId();
    }

    public static MwnFightBeforeEvent valueOf(AbstractCreature attacker,AbstractCreature defender) {
         MwnFightBeforeEvent event = new MwnFightBeforeEvent();
        event.attacker = attacker;
        event.defender = defender;
        return event;
    }

    public AbstractCreature getAttacker() {
        return attacker;
    }

    public void setAttacker(AbstractCreature attacker) {
        this.attacker = attacker;
    }

    public AbstractCreature getDefender() {
        return defender;
    }

    public void setDefender(AbstractCreature defender) {
        this.defender = defender;
    }
}

