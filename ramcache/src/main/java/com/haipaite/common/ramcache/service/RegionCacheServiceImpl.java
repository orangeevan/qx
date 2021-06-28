//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.haipaite.common.ramcache.service;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap.Builder;
import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.CachedEntityConfig;
import com.haipaite.common.ramcache.anno.InitialConfig;
import com.haipaite.common.ramcache.exception.ConfigurationException;
import com.haipaite.common.ramcache.exception.InvaildEntityException;
import com.haipaite.common.ramcache.exception.StateException;
import com.haipaite.common.ramcache.orm.Accessor;
import com.haipaite.common.ramcache.orm.Querier;
import com.haipaite.common.ramcache.persist.AbstractListener;
import com.haipaite.common.ramcache.persist.Element;
import com.haipaite.common.ramcache.persist.Persister;
import com.haipaite.common.utility.collection.ConcurrentHashSet;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionCacheServiceImpl<PK extends Comparable<PK> & Serializable, T extends IEntity<PK>> implements RegionCacheService<PK, T>, RegionEnhanceService<PK, T> {
    private static final Logger logger = LoggerFactory.getLogger(RegionCacheServiceImpl.class);
    private boolean initialize;
    private Class<T> entityClz;
    private CachedEntityConfig config;
    private Accessor accessor;
    private Querier querier;
    private Persister persister;
    private RegionCacheServiceImpl.WeakEntityHolder<T> entityHolder;
    private ConcurrentMap<String, ConcurrentHashMap<Object, ConcurrentHashMap<PK, T>>> cache;
    private ConcurrentHashMap<IndexValue, ReentrantLock> indexLocks = new ConcurrentHashMap();
    private ConcurrentHashMap<PK, ReentrantLock> pkLocks = new ConcurrentHashMap();
    private ConcurrentHashSet<PK> removing = new ConcurrentHashSet();
    private ReentrantReadWriteLock removingLock = new ReentrantReadWriteLock();

    public RegionCacheServiceImpl() {
    }

    public synchronized void initialize(CachedEntityConfig config, Persister persister, Accessor accessor, Querier querier) {
        if (this.initialize) {
            throw new StateException("重复初始化异常");
        } else {
            this.initFileds(config, persister, accessor, querier);
            this.initCaches(config, querier);
            this.initialize = true;
        }
    }

    @Override
    public Collection<T> load(IndexValue idx) {
        this.uninitializeThrowException();
        if (!this.config.hasIndexField(idx.getName())) {
            throw new StateException("实体[" + this.entityClz.getName() + "]没有索引属性域[" + idx.getName() + "]");
        } else {
            ConcurrentHashMap<PK, T> result = this.getIndexValueMap(idx);
            if (result != null) {
                return Collections.unmodifiableCollection(result.values());
            } else {
                Lock lock = this.getIndexValueLock(idx);
                lock.lock();

                Iterator<T> var6;
                try {
                    result = this.getIndexValueMap(idx);
                    if (result != null) {
                        Collection var16 = Collections.unmodifiableCollection(result.values());
                        return var16;
                    }

                    result = this.createIndexValueMap(idx);
                    ReadLock removingLock = this.removingLock.readLock();
                    removingLock.lock();

                    try {
                        List<T> current = this.querier.list(this.entityClz, this.config.getIndexQuery(idx.getName()), new Object[]{idx.getValue()});
                        if (!current.isEmpty()) {
                            var6 = current.iterator();

                            while (var6.hasNext()) {
                                T entity = var6.next();
                                if (!this.removing.contains(entity.getId())) {
                                    entity = this.entityHolder.putIfAbsentEntity(entity);
                                    result.put(entity.getId(), entity);
                                }
                            }

                            return Collections.unmodifiableCollection(result.values());
                        }

                        return Collections.EMPTY_SET;
                    } finally {
                        removingLock.unlock();
                    }
                } finally {
                    lock.unlock();
                    this.indexLocks.remove(idx);
                }

            }
        }
    }

    @Override
    public T get(IndexValue idx, PK id) {
        this.uninitializeThrowException();
        if (!this.config.hasIndexField(idx.getName())) {
            throw new StateException("实体[" + this.entityClz.getName() + "]没有索引属性域[" + idx.getName() + "]");
        } else {
            ReadLock rLock = this.removingLock.readLock();
            rLock.lock();

            ConcurrentHashMap<PK, T> result;
            try {
                if (this.removing.contains(id)) {
                    return null;
                }
            } finally {
                rLock.unlock();
            }

            result = this.getIndexValueMap(idx);
            if (result != null) {
                return result.get(id);
            } else {
                Lock lock = this.getIndexValueLock(idx);
                lock.lock();

                T var6;
                try {
                    result = this.getIndexValueMap(idx);
                    if (result == null) {
                        result = this.createIndexValueMap(idx);
                        rLock.lock();

                        try {
                            List<T> current = this.querier.list(this.entityClz, this.config.getIndexQuery(idx.getName()), new Object[]{idx.getValue()});
                            Iterator<T> var7;
                            if (current.isEmpty()) {
                                return null;
                            }

                            var7 = current.iterator();

                            while (var7.hasNext()) {
                                T entity = var7.next();
                                if (!this.removing.contains(entity.getId())) {
                                    entity = this.entityHolder.putIfAbsentEntity(entity);
                                    result.put(entity.getId(), entity);
                                }
                            }

                            return result.get(id);
                        } finally {
                            rLock.unlock();
                        }
                    }

                    var6 = result.get(id);
                } finally {
                    lock.unlock();
                    this.indexLocks.remove(idx);
                }

                return var6;
            }
        }
    }

    @Override
    public T create(T entity) {
        this.uninitializeThrowException();
        if (entity.getId() == null) {
            throw new InvaildEntityException("新创建的实体必须指定主键");
        } else {
            PK id = (PK) entity.getId();
            Lock lock = this.getPkLock(id);
            lock.lock();

            T var30;
            try {
                T prev = this.entityHolder.getPrevEntity(entity);
                if (prev != null) {
                    throw new InvaildEntityException("实体主键[" + entity.getId() + "]重复");
                }

                T current = this.accessor.load(this.entityClz, (Serializable) id);
                if (current != null) {
                    throw new InvaildEntityException("实体主键[" + entity.getId() + "]重复");
                }

                this.persister.put(Element.saveOf(entity));
                entity = this.entityHolder.putIfAbsentEntity(entity);
                Iterator var6 = this.config.getIndexValues(entity).entrySet().iterator();

                while (var6.hasNext()) {
                    Entry<String, Object> entry = (Entry) var6.next();
                    IndexValue idx = IndexValue.valueOf((String) entry.getKey(), entry.getValue());
                    Lock idxLock = this.getIndexValueLock(idx);
                    idxLock.lock();

                    try {
                        ConcurrentHashMap<PK, T> currents = this.getIndexValueMap(idx);
                        if (currents == null) {
                            currents = this.createIndexValueMap(idx);
                            ReadLock rLock = this.removingLock.readLock();
                            rLock.lock();

                            try {
                                List<T> queryResult = this.querier.list(this.entityClz, this.config.getIndexQuery(idx.getName()), new Object[]{idx.getValue()});
                                Iterator<T> var13 = queryResult.iterator();

                                while (var13.hasNext()) {
                                    T queryEntity = var13.next();
                                    if (!this.removing.contains(queryEntity.getId())) {
                                        queryEntity = this.entityHolder.putIfAbsentEntity(queryEntity);
                                        currents.put(queryEntity.getId(), queryEntity);
                                    }
                                }
                            } finally {
                                rLock.unlock();
                            }
                        }

                        currents.put(id, entity);
                    } finally {
                        idxLock.unlock();
                        this.indexLocks.remove(idx);
                    }
                }

                var30 = entity;
            } finally {
                lock.unlock();
                this.pkLocks.remove(id);
            }

            return var30;
        }
    }

    @Override
    public void remove(T entity) {
        this.uninitializeThrowException();
        PK id = (PK) entity.getId();
        ReadLock rLock = this.removingLock.readLock();
        WriteLock wLock = this.removingLock.writeLock();
        rLock.lock();

        try {
            if (this.removing.contains(id)) {
                return;
            }
        } finally {
            rLock.unlock();
        }

        Lock lock = this.getPkLock(id);
        lock.lock();

        try {
            rLock.lock();

            try {
                if (this.removing.contains(id)) {
                    return;
                }
            } finally {
                rLock.unlock();
            }

            wLock.lock();

            try {
                this.removing.add(id);
            } finally {
                wLock.unlock();
            }

            this.persister.put(Element.removeOf((Serializable) id, this.entityClz));
            Iterator var6 = this.config.getIndexValues(entity).entrySet().iterator();

            while (var6.hasNext()) {
                Entry<String, Object> entry = (Entry) var6.next();
                IndexValue idx = IndexValue.valueOf((String) entry.getKey(), entry.getValue());
                ConcurrentHashMap<PK, T> currents = this.getIndexValueMap(idx);
                if (currents != null) {
                    currents.remove(id);
                }
            }

        } finally {
            lock.unlock();
            this.pkLocks.remove(id);
        }
    }

    @Override
    public void clear(IndexValue idx) {
        Lock lock = this.getIndexValueLock(idx);
        lock.lock();

        try {
            ((ConcurrentHashMap) this.cache.get(idx.getName())).remove(idx.getValue());
        } finally {
            lock.unlock();
            this.indexLocks.remove(idx);
        }

    }

    @Override
    public void writeBack(PK id, T entity) {
        this.uninitializeThrowException();
        Lock rLock = this.removingLock.readLock();
        rLock.lock();

        try {
            if (this.removing.contains(id)) {
                if (logger.isWarnEnabled()) {
                    logger.warn("尝试更新处于待删除状态的实体[{}:{}],操作将被忽略", this.entityClz.getSimpleName(), id);
                }

                return;
            }
        } finally {
            rLock.unlock();
        }

        this.persister.put(Element.updateOf(entity));
    }

    @Override
    public void changeIndexValue(String name, T entity, Object prev) {
        this.uninitializeThrowException();
        PK id = (PK) entity.getId();
        Object current = this.config.getIndexValues(entity).get(name);
        IndexValue idx = IndexValue.valueOf(name, prev);
        Lock lock = this.getIndexValueLock(idx);
        lock.lock();

        ConcurrentHashMap result;
        ReadLock removingLock;
        List queryResult;
        Iterator<T> var11;
        T queryEntity;
        try {
            result = this.getIndexValueMap(idx);
            if (result == null) {
                result = this.createIndexValueMap(idx);
                removingLock = this.removingLock.readLock();
                removingLock.lock();

                try {
                    queryResult = this.querier.list(this.entityClz, this.config.getIndexQuery(idx.getName()), new Object[]{idx.getValue()});
                    var11 = queryResult.iterator();

                    while (var11.hasNext()) {
                        queryEntity = var11.next();
                        if (!this.removing.contains(queryEntity.getId())) {
                            queryEntity = this.entityHolder.putIfAbsentEntity(queryEntity);
                            result.put(queryEntity.getId(), queryEntity);
                        }
                    }
                } finally {
                    removingLock.unlock();
                }
            }

            result.remove(id);
        } finally {
            lock.unlock();
            this.indexLocks.remove(idx);
        }

        idx = IndexValue.valueOf(name, current);
        lock = this.getIndexValueLock(idx);
        lock.lock();

        try {
            result = this.getIndexValueMap(idx);
            if (result == null) {
                result = this.createIndexValueMap(idx);
                removingLock = this.removingLock.readLock();
                removingLock.lock();

                try {
                    queryResult = this.querier.list(this.entityClz, this.config.getIndexQuery(idx.getName()), new Object[]{idx.getValue()});
                    var11 = queryResult.iterator();

                    while (var11.hasNext()) {
                        queryEntity = var11.next();
                        if (!this.removing.contains(queryEntity.getId())) {
                            queryEntity = this.entityHolder.putIfAbsentEntity(queryEntity);
                            result.put(queryEntity.getId(), queryEntity);
                        }
                    }
                } finally {
                    removingLock.unlock();
                }
            }

            removingLock = this.removingLock.readLock();
            removingLock.lock();

            try {
                if (!this.removing.contains(id)) {
                    result.put(id, entity);
                }
            } finally {
                removingLock.unlock();
            }
        } finally {
            lock.unlock();
            this.indexLocks.remove(idx);
        }

    }

    @Override
    public CachedEntityConfig getEntityConfig() {
        return this.config;
    }

    @Override
    public Persister getPersister() {
        return this.persister;
    }

    private ConcurrentHashMap<PK, T> createIndexValueMap(IndexValue idx) {
        ConcurrentHashMap<PK, T> result = new ConcurrentHashMap();
        ConcurrentHashMap<PK, T> prev = (ConcurrentHashMap) ((ConcurrentHashMap) this.cache.get(idx.getName())).putIfAbsent(idx.getValue(), result);
        return prev == null ? result : prev;
    }

    private ConcurrentHashMap<PK, T> getIndexValueMap(IndexValue idx) {
        return (ConcurrentHashMap) ((ConcurrentHashMap) this.cache.get(idx.getName())).get(idx.getValue());
    }

    private Lock getIndexValueLock(IndexValue idx) {
        ReentrantLock result = (ReentrantLock) this.indexLocks.get(idx);
        if (result == null) {
            result = new ReentrantLock();
            ReentrantLock prevLock = (ReentrantLock) this.indexLocks.putIfAbsent(idx, result);
            result = prevLock != null ? prevLock : result;
        }

        return result;
    }

    private Lock getPkLock(PK id) {
        ReentrantLock result = (ReentrantLock) this.pkLocks.get(id);
        if (result == null) {
            result = new ReentrantLock();
            ReentrantLock prevLock = (ReentrantLock) this.pkLocks.putIfAbsent(id, result);
            result = prevLock != null ? prevLock : result;
        }

        return result;
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
            private void remove(PK id) {
                Lock lock = RegionCacheServiceImpl.this.removingLock.writeLock();
                lock.lock();

                try {
                    RegionCacheServiceImpl.this.removing.remove(id);
                } finally {
                    lock.unlock();
                }

            }

            @Override
            protected void onRemoveSuccess(Serializable id) {
                this.remove((PK) id);
            }

            @Override
            protected void onRemoveError(Serializable id, RuntimeException ex) {
                this.remove((PK) id);
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
        this.entityHolder = new RegionCacheServiceImpl.WeakEntityHolder();
        switch (cached.type()) {
            case LRU:
                Builder<String, ConcurrentHashMap<Object, ConcurrentHashMap<PK, T>>> builder = (new Builder()).initialCapacity(cached.initialCapacity()).maximumWeightedCapacity((long) config.getCachedSize()).concurrencyLevel(cached.concurrencyLevel());
                this.cache = builder.build();
                break;
            case MANUAL:
                this.cache = new ConcurrentHashMap(cached.initialCapacity(), 0.75F, cached.concurrencyLevel());
                break;
            default:
                throw new ConfigurationException("未支持的缓存管理类型[" + cached.type() + "]");
        }

        Iterator var8 = config.getIndexNames().iterator();

        while (var8.hasNext()) {
            String name = (String) var8.next();
            this.cache.put(name, new ConcurrentHashMap());
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

            while (var5.hasNext()) {
                T entity = var5.next();
                PK pk = entity.getId();
                Map<String, Object> indexMap = config.getIndexValues(entity);
                entity = this.entityHolder.putIfAbsentEntity(entity);

                ConcurrentHashMap indexCache;
                for (Iterator var9 = indexMap.entrySet().iterator(); var9.hasNext(); indexCache.put(pk, entity)) {
                    Entry<String, Object> entry = (Entry) var9.next();
                    String name = (String) entry.getKey();
                    Object value = entry.getValue();
                    IndexValue idx = IndexValue.valueOf(name, value);
                    indexCache = this.getIndexValueMap(idx);
                    if (indexCache == null) {
                        indexCache = this.createIndexValueMap(idx);
                    }
                }
            }

        }
    }

    private static class WeakEntityHolder<T extends IEntity> {
        private final WeakHashMap<T, WeakReference<T>> entities = new WeakHashMap();
        private final ReentrantReadWriteLock entitiesLock = new ReentrantReadWriteLock();

        public WeakEntityHolder() {
        }

        private T getPrevEntity(T entity) {
            Lock lock = this.entitiesLock.readLock();
            lock.lock();

            T var4;
            try {
                WeakReference<T> ref = (WeakReference) this.entities.get(entity);
                if (ref != null) {
                    var4 = ref.get();
                    return var4;
                }

                var4 = null;
            } finally {
                lock.unlock();
            }

            return var4;
        }

        private T putIfAbsentEntity(T entity) {
            Lock lock = this.entitiesLock.writeLock();
            lock.lock();

            T var5;
            try {
                WeakReference<T> value = (WeakReference) this.entities.get(entity);
                if (value != null) {
                    T prev = value.get();
                    if (prev != null) {
                        var5 = prev;
                        return var5;
                    }
                }

                this.entities.put(entity, new WeakReference(entity));
                var5 = entity;
            } finally {
                lock.unlock();
            }

            return var5;
        }
    }
}
