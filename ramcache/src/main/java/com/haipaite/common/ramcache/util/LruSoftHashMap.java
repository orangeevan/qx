package com.haipaite.common.ramcache.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class LruSoftHashMap<K, V> {
    private Map<K, Reference<V>> cacheMap = new HashMap<>();
    private final Map<K, V> hardCache;
    private final ReferenceQueue<V> refQueue = new ReferenceQueue<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = this.lock.writeLock();

    public LruSoftHashMap(Map<K, V> map) {
        if (map == null) {
            throw new NullPointerException("map is null Exception");
        }
        this.hardCache = map;
    }

    public int getCacheMapSize() {
        return this.cacheMap.size();
    }

    public V get(K key) {
        this.readLock.lock();
        try {
            V result = this.hardCache.get(key);
            if (result != null) {
                return result;
            }

            Reference<V> ref = this.cacheMap.get(key);
            if (ref != null) {
                result = ref.get();
                if (result == null) {
                    this.cacheMap.remove(key);
                } else {
                    this.hardCache.put(key, result);
                }
            }
            return result;
        } finally {
            this.readLock.unlock();
        }
    }

    public void put(K key, V value) {
        this.writeLock.lock();
        try {
            Reference<V> entry = new Entry(key, value, this.refQueue);
            this.cacheMap.put(key, entry);
            this.hardCache.put(key, value);
        } finally {
            this.writeLock.unlock();
        }
    }

    public V remove(K key) {
        this.writeLock.lock();
        try {
            V result = null;
            result = this.hardCache.remove(key);
            if (result == null) {
                Reference<V> ref = this.cacheMap.remove(key);
                if (ref != null) {
                    result = ref.get();
                }
            }
            return result;
        } finally {
            this.writeLock.unlock();
        }
    }

    private class Entry extends SoftReference<V> {
        private K key;

        Entry(K key, V referent, ReferenceQueue<? super V> q) {
            super(referent, q);
            this.key = key;
        }


        K getKey() {
            return this.key;
        }
    }

    public Collection<V> values() {
        return this.hardCache.values();
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcach\\util\LruSoftHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */