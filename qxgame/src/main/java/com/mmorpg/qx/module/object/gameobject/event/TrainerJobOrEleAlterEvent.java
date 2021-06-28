package com.mmorpg.qx.module.object.gameobject.event;

import com.haipaite.common.event.event.IEvent;
import com.haipaite.common.ramcache.IEntity;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;

/**
 * @author wang ke
 * @description: 驯养师职业或元素属性点改变
 * @since 11:28 2021/3/18
 */
public class TrainerJobOrEleAlterEvent implements IEvent {

    private AbstractTrainerCreature creature;

    private AttrType jobAttrType;

    private AttrType eleAttrType;

    private int eleAttrBefore;

    private Reason reason;

    public static TrainerJobOrEleAlterEvent valueOf(AbstractTrainerCreature creature, AttrType eleAttrType, AttrType jobAttrType, Reason reason, int eleAttrBefore) {
        TrainerJobOrEleAlterEvent event = new TrainerJobOrEleAlterEvent();
        event.creature = creature;
        event.jobAttrType = jobAttrType;
        event.eleAttrType = eleAttrType;
        event.reason = reason;
        event.eleAttrBefore = eleAttrBefore;
        return event;
    }

    public AbstractTrainerCreature getCreature() {
        return creature;
    }

    public void setCreature(AbstractTrainerCreature creature) {
        this.creature = creature;
    }

    public AttrType getJobAttrType() {
        return jobAttrType;
    }

    public void setJobAttrType(AttrType jobAttrType) {
        this.jobAttrType = jobAttrType;
    }

    public AttrType getEleAttrType() {
        return eleAttrType;
    }

    public void setEleAttrType(AttrType eleAttrType) {
        this.eleAttrType = eleAttrType;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public int getEleAttrBefore() {
        return eleAttrBefore;
    }

    public void setEleAttrBefore(int eleAttrBefore) {
        this.eleAttrBefore = eleAttrBefore;
    }

    @Override
    public long getOwner() {
        return creature.getDispatcherHashCode();
    }
}
