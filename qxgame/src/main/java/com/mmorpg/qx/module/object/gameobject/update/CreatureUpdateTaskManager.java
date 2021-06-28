package com.mmorpg.qx.module.object.gameobject.update;

import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.threadpool.IdentityEventExecutorGroup;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wang ke
 * @description: 简单更新任务管理,如果DB相关业务，防止操作过于频繁
 * @since 11:02 2020-08-13
 */
public class CreatureUpdateTaskManager<T> {

    private ConcurrentHashMap<CreatureUpdateType, AtomicBoolean> updateTaskState;

    public CreatureUpdateTaskManager(CreatureUpdateType... types) {
        updateTaskState = new ConcurrentHashMap<>(types.length);
        for (CreatureUpdateType type : types) {
            updateTaskState.put(type, new AtomicBoolean(false));
        }
    }

    private boolean canUpdate(CreatureUpdateType type) {
        if (!updateTaskState.containsKey(type)) {
            return false;
        }
        return updateTaskState.get(type).compareAndSet(false, true);
    }

    private boolean resetUpdate(CreatureUpdateType type) {
        if (!updateTaskState.containsKey(type)) {
            return false;
        }
        updateTaskState.get(type).set(false);
        return true;
    }

    /***
     * 添加更新任务,PS:这里的延迟任务是不保证关服时一定能够执行完成.如果必须是在关服前执行的任务.请在玩家登出或者停服接口处执行
     * @param type
     * @param runable
     * @param delay
     * @param timeUnit
     * @return
     */
    public boolean addUpdateTask(CreatureUpdateType type, AbstractDispatcherHashCodeRunable runable, int delay, TimeUnit timeUnit) {
        if (!canUpdate(type)) {
            return false;
        }
        IdentityEventExecutorGroup.addScheduleTask(new AbstractDispatcherHashCodeRunable() {
            @Override
            public int getDispatcherHashCode() {
                return runable.getDispatcherHashCode();
            }

            @Override
            public String name() {
                return runable.name();
            }

            @Override
            public void doRun() {
                try {
                    runable.doRun();
                } finally {
                    resetUpdate(type);
                }

            }
        }, delay, timeUnit);
        return true;
    }

    /***
     * 添加分钟延迟任务
     * @param type
     * @param runable
     * @param delayMinute
     * @return
     */
    public boolean addUpdateTaskDelayMinute(CreatureUpdateType type, AbstractDispatcherHashCodeRunable runable, int delayMinute) {
        return addUpdateTask(type, runable, delayMinute, TimeUnit.MINUTES);
    }

    /***
     * 添加秒级延迟任务
     * @param type
     * @param runable
     * @param delaySecond
     * @return
     */
    public boolean addUpdateTaskDelaySecond(CreatureUpdateType type, AbstractDispatcherHashCodeRunable runable, int delaySecond) {
        return addUpdateTask(type, runable, delaySecond, TimeUnit.SECONDS);
    }

    public boolean addUpdateTaskNoDelay(CreatureUpdateType type, AbstractDispatcherHashCodeRunable runable) {
        if (!canUpdate(type)) {
            return false;
        }
        IdentityEventExecutorGroup.addTask(new AbstractDispatcherHashCodeRunable() {
            @Override
            public int getDispatcherHashCode() {
                return runable.getDispatcherHashCode();
            }

            @Override
            public String name() {
                return runable.name();
            }

            @Override
            public void doRun() {
                try {
                    runable.doRun();
                } finally {
                    resetUpdate(type);
                }

            }
        });
        return true;
    }
}
