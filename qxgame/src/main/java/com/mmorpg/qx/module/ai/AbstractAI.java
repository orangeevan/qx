package com.mmorpg.qx.module.ai;

import com.mmorpg.qx.module.ai.event.Event;
import com.mmorpg.qx.module.ai.event.IEventHandler;
import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 卡招AI行为基本都是被动触发，AI无状态，可以动态切换，可以理解为rpg弱化版
 *
 * @author wang ke
 * @since v1.0 2020年7月29日
 */
public abstract class AbstractAI {

    private ConcurrentHashMap<Event, IEventHandler> eventHandlers = new ConcurrentHashMap<>();
    /**
     * 处理对应AI在该状态下行为,不同AI，
     */
    private ConcurrentHashMap<AIState, IStateHandler> stateHandlers = new ConcurrentHashMap<>();

    /**
     * @param event The event that needs to be handled
     */
    public synchronized void handleEvent(Event event, AbstractCreature owner, Object params) {
        if (owner.isAlreadyDead()) {
            return;
        }
        IEventHandler eventHandler = eventHandlers.get(event);
        if (eventHandler != null) {
            eventHandler.handleEvent(event, owner, params);
        }
    }

    public synchronized void handleState(AIState aiState, AbstractTrainerCreature owner, Object... params) {
        if (owner.isAlreadyDead()) {
            return;
        }
        if (owner.getRoom().isEnd()) {
            return;
        }
        IStateHandler handler = stateHandlers.get(aiState);
        if (handler != null) {
            handler.handleState(owner, params);
        }
    }

    /**
     * 装配事件处理器
     *
     * @param event        之所以把这个参数显示的放置出来而不是直接eventHandler.getEvent(),是为了直观的查看装配的类型
     * @param eventHandler
     */
    protected void addEventHandler(Event event, IEventHandler eventHandler) {
        if (event != eventHandler.getEvent()) {
            throw new RuntimeException(String.format("event[%s],eventHandler's event[%s]", event, eventHandler.getEvent()));
        }
        this.eventHandlers.put(eventHandler.getEvent(), eventHandler);
    }

    protected void addEventHandler(IEventHandler eventHandler) {
        this.eventHandlers.put(eventHandler.getEvent(), eventHandler);
    }

    /**
     * 装配状态处理器
     *
     * @param aiState
     * @param stateHandler
     */
    protected void addStateHandler(AIState aiState, IStateHandler stateHandler) {
        if (aiState != stateHandler.getState()) {
            throw new RuntimeException(String.format("AIState[%s],stateHandler's AIState[%s]", aiState, stateHandler.getState()));
        }
        this.stateHandlers.put(stateHandler.getState(), stateHandler);
    }

    protected void addStateHandler(IStateHandler stateHandler) {
        addStateHandler(stateHandler.getState(), stateHandler);
    }

    public synchronized void stop(AbstractCreature owner) {
        owner.getMoveController().stopMoving();
    }

}
