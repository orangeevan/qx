package com.mmorpg.qx.common.observer;


import com.haipaite.common.utility.collection.ConcurrentHashSet;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 观察者模式
 *
 * @author: yuanchengyan
 * @description:
 * @since 17:06 2021/4/22
 */
public class ObserverController {

    private ConcurrentHashMap<Class, Subject> map = new ConcurrentHashMap<Class, Subject>();

    public void attach(Class clazz, Function function, Supplier<Boolean> remove) {
        Subject subject = map.computeIfAbsent(clazz, k -> new Subject());
        subject.addObserver(new Observer(function, remove));
    }

    /**
     * 添加永久事件
     *
     * @param clazz
     * @param function
     */
    public void attachForEver(Class clazz, Function function) {
        Subject subject = map.computeIfAbsent(clazz, k -> new Subject());
        subject.addObserver(new Observer(function, null));
    }

    /**
     * 添加执行一次事件
     *
     * @param clazz
     * @param function
     */
    public void attachOne(Class clazz, Function function) {
        Subject subject = map.computeIfAbsent(clazz, k -> new Subject());
        subject.addObserver(new Observer(function, () -> true));
    }

    /**
     * 触发
     *
     * @param clazz
     */
    public void fire(Class clazz) {
        Subject subject = map.get(clazz);
        if (Objects.isNull(subject)) {
            return;
        }
        subject.update();

    }

    /**
     * 观察者
     */
    private static class Observer {
        private Function function;
        private Supplier<Boolean> remove;
        private int version;

        private Observer(Function function, Supplier<Boolean> remove) {
            this.function = function;
            this.remove = remove;
        }

    }

    private static class Subject {
        private static final Observer[] ZERO_OBSERVER_ARRAY = new Observer[]{};
        private ConcurrentHashSet<Observer> observers = new ConcurrentHashSet<>();
        private AtomicInteger version = new AtomicInteger();

        private void addObserver(Observer o) {
            o.version = version.incrementAndGet();
            observers.add(o);
        }

        private synchronized void update() {
            Observer[] array = observers.toArray(ZERO_OBSERVER_ARRAY);
            if (array.length == 0) {
                return;
            }

            int index = ThreadLocalRandom.current().nextInt(array.length);
            int version = this.version.get();
            for (int i = 0; i < array.length; i++) {
                //无序执行各个观察者(保证观察者与顺序无关)
                Observer o = array[(i + index) % array.length];
                if (o.version > version) {
                    continue;
                }
                try {
                    o.function.apply();
                } finally {
                    if ((o.remove != null && o.remove.get())) {
                        observers.remove(o);
                    }
                }

            }
        }
    }

    @FunctionalInterface
    public interface Function {
        void apply();
    }
}

