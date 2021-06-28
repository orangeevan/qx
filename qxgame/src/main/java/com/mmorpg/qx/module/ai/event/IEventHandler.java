package com.mmorpg.qx.module.ai.event;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * 事件处理器，由外部向AI抛事件，改变AI状态行为
 *
 * @author wang ke
 * @since v1.0 2020年07月30日
 */
public interface IEventHandler<T> {

    /**
     * 处理的事件类型
     *
     * @return
     */
    Event getEvent();

    /**
     * 处理事件
     *
     * @param event
     * @param owner
     * @param parms
     */
    void handleEvent(Event event, AbstractCreature owner, T object);

}
