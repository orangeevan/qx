//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.haipaite.common.ramcache;

import com.haipaite.common.ramcache.anno.CacheUnit;
import com.haipaite.common.ramcache.anno.CachedEntityConfig;
import com.haipaite.common.ramcache.exception.ConfigurationException;
import com.haipaite.common.ramcache.exception.StateException;
import com.haipaite.common.ramcache.orm.Accessor;
import com.haipaite.common.ramcache.orm.Querier;
import com.haipaite.common.ramcache.persist.Persister;
import com.haipaite.common.ramcache.persist.PersisterConfig;
import com.haipaite.common.ramcache.persist.PersisterType;
import com.haipaite.common.ramcache.persist.QueuePersister;
import com.haipaite.common.ramcache.persist.TimingConsumer;
import com.haipaite.common.ramcache.persist.TimingPersister;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.ramcache.service.EntityCacheServiceImpl;
import com.haipaite.common.ramcache.service.EntityCaffeineCacheServiceImpl;
import com.haipaite.common.ramcache.service.RegionCacheService;
import com.haipaite.common.ramcache.service.RegionCacheServiceImpl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class ServiceManager implements ServiceManagerMBean {
    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
    private final Accessor accessor;
    private final Querier querier;
    private final Map<String, PersisterConfig> persisterConfigs;
    private final Map<Class<? extends IEntity>, CachedEntityConfig> entityConfigs = new HashMap();
    private final Map<String, Persister> persisters = new HashMap();
    private final Map<Class<? extends IEntity>, EntityCacheService> entityServices = new HashMap();
    private final Map<Class<? extends IEntity>, RegionCacheService> regionServices = new HashMap();

    public ServiceManager(Set<Class<IEntity>> entityClasses, Accessor accessor, Querier querier, Map<String, Integer> constants, Map<String, PersisterConfig> persisterConfigs) {
        Assert.notNull(accessor, "存储器不能为空");
        Assert.notNull(querier, "查询器不能为空");
        Assert.notNull(entityClasses, "实体类配置集合不能为空");
        this.accessor = accessor;
        this.querier = querier;
        this.persisterConfigs = persisterConfigs;
        Iterator var6 = entityClasses.iterator();

        while (var6.hasNext()) {
            Class<? extends IEntity> clz = (Class) var6.next();
            if (!CachedEntityConfig.isVaild(clz, constants)) {
                throw new ConfigurationException("无效的缓存实体[" + clz.getName() + "]配置");
            }

            CachedEntityConfig config = CachedEntityConfig.valueOf(clz, constants);
            this.entityConfigs.put(clz, config);
        }

    }

    public EntityCacheService getEntityService(Class<? extends IEntity> clz) {
        CachedEntityConfig config = (CachedEntityConfig) this.entityConfigs.get(clz);
        if (config == null) {
            throw new StateException("类[" + clz.getName() + "]不是有效的缓存实体");
        } else if (!config.cacheUnitIs(CacheUnit.ENTITY)) {
            throw new StateException("实体[" + clz.getName() + "]的缓存单位不是[" + CacheUnit.ENTITY + "]");
        } else {
            EntityCacheService result = (EntityCacheService) this.entityServices.get(clz);
            return result;
        }
    }

    public RegionCacheService getRegionService(Class<? extends IEntity> clz) {
        CachedEntityConfig config = (CachedEntityConfig) this.entityConfigs.get(clz);
        if (config == null) {
            throw new StateException("类[" + clz.getName() + "]不是有效的缓存实体");
        } else if (!config.cacheUnitIs(CacheUnit.REGION)) {
            throw new StateException("实体[" + clz.getName() + "]的缓存单位不是[" + CacheUnit.REGION + "]");
        } else {
            RegionCacheService result = (RegionCacheService) this.regionServices.get(clz);
            return result != null ? result : this.createRegionService(clz);
        }
    }

    public void shutdown() {
        logger.info("开始停止缓存更新,现有缓存入库");
        System.err.println("开始停止缓存更新,现有缓存入库");
        Iterator var1 = this.persisters.values().iterator();

        while (var1.hasNext()) {
            Persister queue = (Persister) var1.next();
            queue.shutdown();
        }

        TimingConsumer.shutdownExecutor();
    }

    @Override
    public Map<String, Map<String, String>> getAllPersisterInfo() {
        HashMap<String, Map<String, String>> result = new HashMap();
        Iterator var2 = this.persisters.entrySet().iterator();

        while (var2.hasNext()) {
            Entry<String, Persister> entry = (Entry) var2.next();
            result.put(entry.getKey(), ((Persister) entry.getValue()).getInfo());
        }

        return result;
    }

    @Override
    public Map<String, String> getPersisterInfo(String name) {
        Persister persister = (Persister) this.persisters.get(name);
        return persister == null ? null : persister.getInfo();
    }

    public Map<String, CachedEntityConfig> getAllCachedEntityConfig() {
        HashMap<String, CachedEntityConfig> result = new HashMap();
        Iterator var2 = this.entityServices.entrySet().iterator();

        Entry entry;
        while (var2.hasNext()) {
            entry = (Entry) var2.next();
            result.put(((Class) entry.getKey()).getName(), ((EntityCacheService) entry.getValue()).getEntityConfig());
        }

        var2 = this.regionServices.entrySet().iterator();

        while (var2.hasNext()) {
            entry = (Entry) var2.next();
            result.put(((Class) entry.getKey()).getName(), ((RegionCacheService) entry.getValue()).getEntityConfig());
        }

        return result;
    }

    private synchronized RegionCacheService createRegionService(Class<? extends IEntity> clz) {
        if (this.regionServices.containsKey(clz)) {
            return (RegionCacheService) this.regionServices.get(clz);
        } else {
            CachedEntityConfig config = (CachedEntityConfig) this.entityConfigs.get(clz);
            Persister queue = this.getPersister(config.getPersisterName());
            RegionCacheServiceImpl result = new RegionCacheServiceImpl();
            result.initialize(config, queue, this.accessor, this.querier);
            this.regionServices.put(config.getClz(), result);
            return result;
        }
    }

    public synchronized EntityCacheService createEntityService(Class<? extends IEntity> clz) {
        if (this.entityServices.containsKey(clz)) {
            return (EntityCacheService) this.entityServices.get(clz);
        } else {
            CachedEntityConfig config = (CachedEntityConfig) this.entityConfigs.get(clz);
            Persister queue = this.getPersister(config.getPersisterName());
            EntityCacheService result;
            if (config.getCached().caffeine()) {
                result = new EntityCaffeineCacheServiceImpl();
            } else {
                result = new EntityCacheServiceImpl();
            }

            result.initialize(config, queue, this.accessor, this.querier);
            this.entityServices.put(config.getClz(), result);
            return result;
        }
    }

    private Persister getPersister(String name) {
        Persister result = (Persister) this.persisters.get(name);
        if (result != null) {
            return result;
        } else if (!this.persisterConfigs.containsKey(name)) {
            throw new ConfigurationException("持久化处理器[" + name + "]的配置信息不存在");
        } else {
            PersisterConfig config = (PersisterConfig) this.persisterConfigs.get(name);
            if (config.getType() == PersisterType.QUEUE) {
                result = new QueuePersister();
            } else {
                result = new TimingPersister();
            }

            result.initialize(name, this.accessor, config.getValue());
            this.persisters.put(name, result);
            return (Persister) result;
        }
    }
}
