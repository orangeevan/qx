package com.mmorpg.qx.module.roundFight.event;

import com.haipaite.common.event.event.IEvent;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * @author: yuanchengyan
 * @description:
 * @since 11:28 2021/4/20
 */
public class MwnFightAfterEvent implements IEvent {
    private AbstractCreature winner;
    private AbstractCreature loser;

    @Override
    public long getOwner() {
        return winner.getObjectId();
    }

    public static MwnFightAfterEvent valueOf(AbstractCreature winner, AbstractCreature loser) {
        MwnFightAfterEvent event = new MwnFightAfterEvent();
        event.winner = winner;
        event.loser = loser;
        return event;
    }

    public AbstractCreature getWinner() {
        return winner;
    }

    public void setWinner(AbstractCreature winner) {
        this.winner = winner;
    }

    public AbstractCreature getLoser() {
        return loser;
    }

    public void setLoser(AbstractCreature loser) {
        this.loser = loser;
    }
}

