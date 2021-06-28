//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.haipaite.common.ramcache.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.CachedEntityConfig;
import com.haipaite.common.ramcache.anno.InitialConfig;
import com.haipaite.common.ramcache.exception.ConfigurationException;
import com.haipaite.common.ramcache.exception.InvaildEntityException;
import com.haipaite.common.ramcache.exception.StateException;
import com.haipaite.common.ramcache.exception.UniqueFieldException;
import com.haipaite.common.ramcache.orm.Accessor;
import com.haipaite.common.ramcache.orm.Querier;
import com.haipaite.common.ramcache.persist.AbstractListener;
import com.haipaite.common.ramcache.persist.Element;
import com.haipaite.common.ramcache.persist.Persister;
import com.haipaite.common.utility.collection.ConcurrentHashSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityCaffeineCacheServiceImpl<PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> implements EntityCacheService<PK, T>, EntityEnhanceService<PK, T> {
    private static final Logger logger = LoggerFactory.getLogger(EntityCaffeineCacheServiceImpl.class);
    private boolean initialize;
    private Class<T> entityClz;
    private CachedEntityConfig config;
    private Accessor accessor;
    private Querier querier;
    private Persister persister;
    private Cache<PK, T> cache;
    private ConcurrentHashMap<PK, ReentrantLock> locks = new ConcurrentHashMap();
    private ConcurrentHashSet<PK> removing = new ConcurrentHashSet();
    private HashMap<String, DualHashBidiMap> uniques;

    public EntityCaffeineCacheServiceImpl() {
    }

    @Override
    public synchronized void initialize(CachedEntityConfig config, Persister persister, Accessor accessor, Querier querier) {
        if (this.initialize) {
            throw new StateException("重复初始化异常");
        } else {
            this.cache = Caffeine.newBuilder().removalListener(new RemovalListener<PK, T>() {
                @Override
                public void onRemoval(@Nullable PK pk, @Nullable T t, @NonNull RemovalCause removalCause) {
                    if (!EntityCaffeineCacheServiceImpl.this.removing.contains(pk)) {
                        EntityCaffeineCacheServiceImpl.logger.info("caffeine 对象【{}】ID【{}】过期，移除前写回DB", t.getClass().getSimpleName(), pk);
                        EntityCaffeineCacheServiceImpl.this.writeBack(pk, t);
                    }

                    EntityCaffeineCacheServiceImpl.logger.info("caffeine 对象【{}】ID【{}】过期,移除缓存", t.getClass().getSimpleName(), pk);
                }
            }).expireAfterAccess((long) config.getExpireMinute(), TimeUnit.MINUTES).maximumSize((long) config.getCachedSize()).build();
            this.initFileds(config, persister, accessor, querier);
            this.initCaches(config, querier);
            this.initialize = true;
        }
    }

    private void releasePkLock(PK id, Lock lock) {
        lock.unlock();
        this.locks.remove(id);
    }

    @Override
    public T load(PK id) {
        this.uninitializeThrowException();
        if (this.removing.contains(id)) {
            return null;
        } else {
            T current = this.cache.getIfPresent(id);
            if (current != null) {
                return current;
            } else {
                Lock lock = this.lockPkLock(id);

                T var10;
                try {
                    Map uniqueValues;
                    if (this.removing.contains(id)) {
                        uniqueValues = null;
                        return null;
                    }

                    current = this.cache.getIfPresent(id);
                    if (current != null) {
                        var10 = current;
                        return var10;
                    }

                    current = this.accessor.load(this.entityClz, (Serializable) id);
                    if (current != null) {
                        if (this.config.hasUniqueField()) {
                            uniqueValues = this.config.getUniqueValues(current);
                            Iterator var5 = uniqueValues.entrySet().iterator();

                            while (var5.hasNext()) {
                                Entry<String, Object> entry = (Entry) var5.next();
                                this.addUniqueValue((String) entry.getKey(), entry.getValue(), id);
                            }
                        }

                        System.err.println("caffine cache the entity: " + id + " entity:" + current);
                        this.cache.put(id, current);
                    }

                    var10 = current;
                } finally {
                    this.releasePkLock(id, lock);
                }

                return var10;
            }
        }
    }

    private Lock lockPkLock(PK id) {
        ReentrantLock lock = (ReentrantLock) this.locks.get(id);
        if (lock == null) {
            lock = new ReentrantLock();
            ReentrantLock prevLock = (ReentrantLock) this.locks.putIfAbsent(id, lock);
            lock = prevLock != null ? prevLock : lock;
        }

        lock.lock();
        return lock;
    }

    @Override
    public T loadOrCreate(PK id, EntityBuilder<PK, T> builder) {
        this.uninitializeThrowException();
        T current;
        if (!this.removing.contains(id)) {
            current = this.cache.getIfPresent(id);
            if (current != null) {
                return current;
            }
        }

        current = null;
        Lock lock = this.lockPkLock(id);

        T var26;
        try {
            current = this.cache.getIfPresent(id);
            if (current != null) {
                T var25 = current;
                return var25;
            }

            current = this.accessor.load(this.entityClz, (Serializable) id);
            boolean flag = this.removing.contains(id);
            Map uniqueValues;
            if (current != null && !flag) {
                if (this.config.hasUniqueField()) {
                    uniqueValues = this.config.getUniqueValues(current);
                    Iterator var27 = uniqueValues.entrySet().iterator();

                    while (var27.hasNext()) {
                        Entry<String, Object> entry = (Entry) var27.next();
                        this.addUniqueValue((String) entry.getKey(), entry.getValue(), id);
                    }
                }
            } else {
                current = builder.newInstance(id);
                if (current == null) {
                    throw new InvaildEntityException("创建主键为[" + id + "]的实体[" + this.entityClz.getName() + "]时返回null");
                }

                if (current.getId() == null) {
                    throw new InvaildEntityException("创建主键为[" + id + "]的实体[" + this.entityClz.getName() + "]时实体的主键值为null");
                }

                if (this.config.hasUniqueField()) {
                    uniqueValues = this.config.getUniqueValues(current);
                    boolean rollback = false;
                    List<Entry<String, Object>> temp = new ArrayList(uniqueValues.size());
                    Iterator var9 = uniqueValues.entrySet().iterator();

                    Entry entry;
                    while (var9.hasNext()) {
                        entry = (Entry) var9.next();
                        String uniqueName = (String) entry.getKey();
                        Object uniqueValue = entry.getValue();
                        WriteLock uniqueLock = this.config.getUniqueWriteLock(uniqueName);
                        uniqueLock.lock();

                        try {
                            DualHashBidiMap unique = (DualHashBidiMap) this.uniques.get(uniqueName);
                            if (unique.containsKey(uniqueValue)) {
                                rollback = true;
                                break;
                            }

                            T chkEntity = this.querier.unique(this.entityClz, this.config.getUniqueQuery(uniqueName), new Object[]{uniqueValue});
                            if (chkEntity != null) {
                                rollback = true;
                                break;
                            }

                            PK prev = (PK) unique.put(uniqueValue, id);
                            if (prev != null) {
                                logger.error("实体[{}]的唯一键值[{}]异常:原主键[{}],当前主键[{}]", new Object[]{this.entityClz.getName(), uniqueValue, prev, id});
                            }

                            temp.add(entry);
                        } finally {
                            uniqueLock.unlock();
                        }
                    }

                    if (rollback) {
                        var9 = temp.iterator();

                        while (var9.hasNext()) {
                            entry = (Entry) var9.next();
                            this.removeUniqueValue((String) entry.getKey(), entry.getValue());
                        }

                        throw new UniqueFieldException();
                    }
                }

                if (flag) {
                    this.removing.remove(id);
                }

                this.persister.put(Element.saveOf(current));
            }

            this.cache.put(id, current);
            var26 = current;
        } finally {
            this.releasePkLock(id, lock);
        }

        return var26;
    }

    @Override
    public void writeBack(PK id, T entity) {
        this.uninitializeThrowException();
        if (this.removing.contains(id)) {
            if (logger.isWarnEnabled()) {
                logger.warn("尝试更新处于待删除状态的实体[{}:{}],操作将被忽略", this.entityClz.getSimpleName(), id);
            }

        } else {
            this.persister.put(Element.updateOf(entity));
        }
    }

    @Override
    public T remove(PK id) {
        this.uninitializeThrowException();
        if (this.removing.contains(id)) {
            return null;
        } else {
            Lock lock = this.lockPkLock(id);

            T current;
            try {
                if (!this.removing.contains(id)) {
                    this.removing.add(id);
                    current = this.cache.getIfPresent(id);
                    if (current != null) {
                        this.cache.invalidate(id);
                        if (this.config.hasUniqueField()) {
                            Map<String, Object> uniqueValues = this.config.getUniqueValues(current);
                            Iterator var5 = uniqueValues.entrySet().iterator();

                            while (var5.hasNext()) {
                                Entry<String, Object> entry = (Entry) var5.next();
                                this.removeUniqueValue((String) entry.getKey(), entry.getValue());
                            }
                        }
                    }

                    this.persister.put(Element.removeOf((Serializable) id, this.entityClz));
                    T var10 = current;
                    return var10;
                }

                current = null;
            } finally {
                this.releasePkLock(id, lock);
            }

            return current;
        }
    }

    @Override
    public void clear(PK id) {
        T current = this.cache.getIfPresent(id);
        if (current != null) {
            Lock lock = this.lockPkLock(id);

            try {
                current = this.cache.getIfPresent(id);
                if (current != null && this.config.hasUniqueField()) {
                    Map<String, Object> uniqueValues = this.config.getUniqueValues(current);
                    Iterator var5 = uniqueValues.entrySet().iterator();

                    while (var5.hasNext()) {
                        Entry<String, Object> entry = (Entry) var5.next();
                        this.removeUniqueValue((String) entry.getKey(), entry.getValue());
                    }
                }
            } finally {
                this.releasePkLock(id, lock);
            }

        }
    }

    @Override
    public CacheFinder<PK, T> getFinder() {
        return null;
    }

    @Override
    public T unique(String name, Object value) {
        this.uninitializeThrowException();
        if (!this.config.hasUniqueField()) {
            throw new UniqueFieldException("实体[" + this.entityClz.getName() + "]没有唯一属性域");
        } else {
            PK id = null;
            ReadLock readLock = this.config.getUniqueReadLock(name);
            readLock.lock();

            try {
                DualHashBidiMap unique = (DualHashBidiMap) this.uniques.get(name);
                id = (PK) unique.get(value);
            } finally {
                readLock.unlock();
            }

            if (id != null) {
                return this.load(id);
            } else {
                T current = this.querier.unique(this.entityClz, this.config.getUniqueQuery(name), new Object[]{value});
                if (current == null) {
                    return current;
                } else {
                    id = (PK) current.getId();
                    Lock lock = this.lockPkLock(id);

                    T prev;
                    try {
                        if (!this.removing.contains(id)) {
                            prev = this.cache.getIfPresent(id);
                            if (prev != null) {
                                T var19 = prev;
                                return var19;
                            }

                            if (this.config.hasUniqueField()) {
                                Map<String, Object> uniqueValues = this.config.getUniqueValues(current);
                                Iterator var9 = uniqueValues.entrySet().iterator();

                                while (var9.hasNext()) {
                                    Entry<String, Object> entry = (Entry) var9.next();
                                    this.addUniqueValue((String) entry.getKey(), entry.getValue(), id);
                                }
                            }

                            this.cache.put(id, current);
                            return current;
                        }

                        prev = null;
                    } finally {
                        this.releasePkLock(id, lock);
                    }

                    return prev;
                }
            }
        }
    }

    @Override
    public boolean hasUniqueValue(String name, Object value) {
        DualHashBidiMap unique = (DualHashBidiMap) this.uniques.get(name);
        if (this.config.getUniqueWriteLock(name).isHeldByCurrentThread()) {
            if (unique.containsKey(value)) {
                return true;
            } else {
                T current = this.querier.unique(this.entityClz, this.config.getUniqueQuery(name), new Object[]{value});
                return current != null;
            }
        } else {
            WriteLock lock = this.config.getUniqueWriteLock(name);
            lock.lock();

            boolean var5;
            try {
                if (!unique.containsKey(value)) {
                    T current = this.querier.unique(this.entityClz, this.config.getUniqueQuery(name), new Object[]{value});
                    boolean var6;
                    if (current == null) {
                        var6 = false;
                        return var6;
                    }

                    var6 = true;
                    return var6;
                }

                var5 = true;
            } finally {
                lock.unlock();
            }

            return var5;
        }
    }

    @Override
    public void replaceUniqueValue(PK id, String name, Object value) {
        if (!this.config.getUniqueWriteLock(name).isHeldByCurrentThread()) {
            throw new StateException("非法执行该方法");
        } else {
            DualHashBidiMap unique = (DualHashBidiMap) this.uniques.get(name);
            unique.removeValue(id);
            unique.put(value, id);
        }
    }

    @Override
    public CachedEntityConfig getEntityConfig() {
        this.uninitializeThrowException();
        return this.config;
    }

    @Override
    public Persister getPersister() {
        return this.persister;
    }

    private void addUniqueValue(String name, Object value, PK id) {
        DualHashBidiMap unique = (DualHashBidiMap) this.uniques.get(name);
        WriteLock uniqueLock = this.config.getUniqueWriteLock(name);
        uniqueLock.lock();

        try {
            PK prev = (PK) unique.put(value, id);
            if (prev != null) {
                logger.error("实体[{}]的唯一键值[{}]异常:原主键[{}],当前主键[{}]", new Object[]{this.entityClz.getName(), value, prev, id});
            }
        } finally {
            uniqueLock.unlock();
        }

    }

    private void removeUniqueValue(String name, Object value) {
        DualHashBidiMap unique = (DualHashBidiMap) this.uniques.get(name);
        WriteLock uniqueLock = this.config.getUniqueWriteLock(name);
        uniqueLock.lock();

        try {
            unique.remove(value);
        } finally {
            uniqueLock.unlock();
        }

    }

    private void uninitializeThrowException() {
        if (!this.initialize) {
            throw new StateException("未完成初始化");
        }
    }

    private void initFileds(CachedEntityConfig config, final Persister persister, Accessor accessor, Querier querier) {
        Cached cached = config.getCached();
        this.config = config;
        this.accessor = accessor;
        this.querier = querier;
        this.entityClz = (Class<T>) config.getClz();
        this.persister = persister;
        this.persister.addListener(this.entityClz, new AbstractListener() {
            @Override
            protected void onRemoveSuccess(Serializable id) {
                EntityCaffeineCacheServiceImpl.this.removing.remove(id);
            }

            @Override
            protected void onRemoveError(Serializable id, RuntimeException ex) {
                EntityCaffeineCacheServiceImpl.this.removing.remove(id);
            }

            @Override
            protected void onSaveError(Serializable id, IEntity entity, RuntimeException ex) {
                if (ex instanceof ConcurrentModificationException) {
                    persister.put(Element.saveOf(entity));
                }

            }

            @Override
            protected void onUpdateError(IEntity entity, RuntimeException ex) {
                if (ex instanceof ConcurrentModificationException) {
                    persister.put(Element.updateOf(entity));
                }

            }
        });
        if (config.hasUniqueField()) {
            this.uniques = config.buildUniqueCache();
        }

    }

    private void initCaches(CachedEntityConfig config, Querier querier) {
        InitialConfig initial = config.getInitialConfig();
        if (initial != null) {
            List<T> entities = null;
            switch (initial.type()) {
                case ALL:
                    entities = querier.all(this.entityClz);
                    break;
                case QUERY:
                    entities = querier.list(this.entityClz, initial.query(), new Object[0]);
                    break;
                default:
                    throw new ConfigurationException("无法按配置[" + initial + "]初始化实体[" + this.entityClz.getName() + "]的缓存");
            }

            Iterator<T> var5 = entities.iterator();

            while (true) {
                T entity;
                PK id;
                do {
                    if (!var5.hasNext()) {
                        return;
                    }

                    entity = var5.next();
                    id = (PK) entity.getId();
                    this.cache.put(id, entity);
                } while (!config.hasUniqueField());

                Map<String, Object> uniqueValues = config.getUniqueValues(entity);
                Iterator var9 = uniqueValues.entrySet().iterator();

                while (var9.hasNext()) {
                    Entry<String, Object> entry = (Entry) var9.next();
                    DualHashBidiMap unique = (DualHashBidiMap) this.uniques.get(entry.getKey());
                    unique.put(entry.getValue(), id);
                }
            }
        }
    }

    @Override
    public T create(PK id, EntityBuilder<PK, T> builder) {
        this.uninitializeThrowException();
        T current;
        if (!this.removing.contains(id)) {
            current = this.cache.getIfPresent(id);
            if (current != null) {
                return current;
            }
        }

        current = null;
        Lock lock = this.lockPkLock(id);

        T var5;
        try {
            current = this.cache.getIfPresent(id);
            if (current == null) {
                boolean flag = this.removing.contains(id);
                current = builder.newInstance(id);
                if (current == null) {
                    throw new InvaildEntityException("创建主键为[" + id + "]的实体[" + this.entityClz.getName() + "]时返回null");
                }

                if (current.getId() == null) {
                    throw new InvaildEntityException("创建主键为[" + id + "]的实体[" + this.entityClz.getName() + "]时实体的主键值为null");
                }

                if (this.config.hasUniqueField()) {
                    Map<String, Object> uniqueValues = this.config.getUniqueValues(current);
                    Iterator var7 = uniqueValues.entrySet().iterator();

                    while (var7.hasNext()) {
                        Entry<String, Object> entry = (Entry) var7.next();
                        String uniqueName = (String) entry.getKey();
                        Object uniqueValue = entry.getValue();
                        WriteLock uniqueLock = this.config.getUniqueWriteLock(uniqueName);
                        uniqueLock.lock();

                        try {
                            DualHashBidiMap unique = (DualHashBidiMap) this.uniques.get(uniqueName);
                            PK prev = (PK) unique.put(uniqueValue, id);
                            if (prev != null) {
                                logger.error("实体[{}]的唯一键值[{}]异常:原主键[{}],当前主键[{}]", new Object[]{this.entityClz.getName(), uniqueValue, prev, id});
                            }
                        } finally {
                            uniqueLock.unlock();
                        }
                    }
                }

                if (flag) {
                    this.removing.remove(id);
                }

                this.persister.put(Element.saveOf(current));
                this.cache.put(id, current);
                T var23 = current;
                return var23;
            }

            var5 = current;
        } finally {
            this.releasePkLock(id, lock);
        }

        return var5;
    }
}
