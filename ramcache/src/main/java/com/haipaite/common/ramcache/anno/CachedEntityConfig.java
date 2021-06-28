package com.haipaite.common.ramcache.anno;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.exception.ConfigurationException;
import com.haipaite.common.ramcache.exception.StateException;
import com.haipaite.common.utility.ReflectionUtility;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.collections.bidimap.DualHashBidiMap;


public class CachedEntityConfig implements Serializable {
    private static final long serialVersionUID = -6067788531240033388L;
    private Class<? extends IEntity> clz;
    private Cached cached;
    private InitialConfig initialConfig;
    private int cachedSize;
    private transient Map<String, Unique> uniques;
    private transient Map<String, Field> uniqueFields;
    private transient Map<String, ReentrantReadWriteLock> uniqueLocks;
    private transient Map<String, Index> indexs;
    private transient Map<String, Field> indexFields;
    private transient Map<String, ReentrantReadWriteLock> indexLocks;
    private transient int expireMinute;

    public static boolean isVaild(Class<?> clz, Map<String, Integer> constants) {
        if (Modifier.isAbstract(clz.getModifiers())) {
            return false;
        }
        if (Modifier.isInterface(clz.getModifiers())) {
            return false;
        }
        if (!IEntity.class.isAssignableFrom(clz)) {
            return false;
        }
        if (!clz.isAnnotationPresent((Class) Cached.class)) {
            return false;
        }
        Cached cached = clz.<Cached>getAnnotation(Cached.class);
        if (!constants.containsKey(cached.size())) {
            throw new ConfigurationException("缓存实体[" + clz.getName() + "]要求的缓存数量定义[" + cached.size() + "]不存在");
        }
        switch (cached.unit()) {
            case ENTITY:
                if ((ReflectionUtility.getDeclaredFieldsWith(clz, Index.class)).length > 0) {
                    throw new ConfigurationException("缓存单位为[" + cached.unit() + "]的实体[" + clz.getName() + "]不支持索引属性配置");
                }


                return true;
            case REGION:
                if ((ReflectionUtility.getDeclaredFieldsWith(clz, Unique.class)).length > 0)
                    throw new ConfigurationException("缓存单位为[" + cached.unit() + "]的实体[" + clz.getName() + "]不支持唯一值属性配置");
                return true;
        }
        throw new ConfigurationException("实体[" + clz.getName() + "]使用了未支持的缓存单位[" + cached.unit() + "]配置");
    }


    public static CachedEntityConfig valueOf(Class<? extends IEntity> clz, Map<String, Integer> constants) {
        CachedEntityConfig result = new CachedEntityConfig();
        result.clz = clz;
        result.cached = clz.<Cached>getAnnotation(Cached.class);
        result.initialConfig = clz.<InitialConfig>getAnnotation(InitialConfig.class);
        result.cachedSize = ((Integer) constants.get(result.cached.size())).intValue();
        result.expireMinute = result.cached.expireMinute();

        Field[] fields = ReflectionUtility.getDeclaredFieldsWith(clz, Unique.class);
        if (fields != null && fields.length > 0) {

            HashMap<String, Unique> uniques = new HashMap<>(fields.length);
            HashMap<String, Field> uniqueFields = new HashMap<>(fields.length);
            HashMap<String, ReentrantReadWriteLock> uniqueLocks = new HashMap<>(fields.length);


            for (Field field : fields) {
                Unique unique = field.<Unique>getAnnotation(Unique.class);
                String name = field.getName();

                ReflectionUtility.makeAccessible(field);
                uniques.put(name, unique);
                uniqueFields.put(name, field);
                uniqueLocks.put(name, new ReentrantReadWriteLock());
            }


            result.uniques = uniques;
            result.uniqueFields = uniqueFields;
            result.uniqueLocks = uniqueLocks;
        }


        fields = ReflectionUtility.getDeclaredFieldsWith(clz, Index.class);
        if (fields != null && fields.length > 0) {

            HashMap<String, Index> indexs = new HashMap<>(fields.length);
            HashMap<String, Field> indexFields = new HashMap<>(fields.length);
            HashMap<String, ReentrantReadWriteLock> indexLocks = new HashMap<>(fields.length);


            for (Field field : fields) {
                Index unique = field.<Index>getAnnotation(Index.class);
                String name = field.getName();

                ReflectionUtility.makeAccessible(field);
                indexs.put(name, unique);
                indexFields.put(name, field);
                indexLocks.put(name, new ReentrantReadWriteLock());
            }


            result.indexs = indexs;
            result.indexFields = indexFields;
            result.indexLocks = indexLocks;
        }

        return result;
    }


    public Collection<String> getIndexNames() {
        if (this.indexs == null) {
            return Collections.EMPTY_SET;
        }

        HashSet<String> result = new HashSet<>(this.indexs.size());
        for (String name : this.indexs.keySet()) {
            result.add(name);
        }
        return result;
    }


    public String getIndexQuery(String name) {
        if (this.indexs == null) {
            throw new StateException("实体[" + this.clz.getName() + "]的索引属性名[" + name + "]无效");
        }
        Index index = this.indexs.get(name);
        if (index == null) {
            throw new StateException("实体[" + this.clz.getName() + "]的索引属性名[" + name + "]无效");
        }
        return index.query();
    }


    public Map<String, Object> getIndexValues(IEntity entity) {
        if (this.indexFields == null) {
            throw new StateException("实体[" + this.clz.getName() + "]没有索引属性配置无法获取索引属性值");
        }

        try {
            Map<String, Object> result = new HashMap<>(this.indexFields.size());
            for (Map.Entry<String, Field> entry : this.indexFields.entrySet()) {
                Object value = ((Field) entry.getValue()).get(entity);
                result.put(entry.getKey(), value);
            }
            return result;
        } catch (Exception e) {
            throw new StateException("无法获取索引属性值:" + e.getMessage());
        }
    }


    public Object getIndexValue(String name, IEntity entity) {
        Map<String, Object> values = getIndexValues(entity);
        if (values.containsKey(name)) {
            return values.get(name);
        }
        throw new StateException("索引属性[" + name + "]不存在");
    }


    public ReentrantReadWriteLock.ReadLock getIndexReadLock(String name) {
        ReentrantReadWriteLock lock = this.indexLocks.get(name);
        if (lock == null) {
            throw new StateException("实体[" + this.clz.getName() + "]的索引属性名[" + name + "]无效");
        }
        return lock.readLock();
    }


    public ReentrantReadWriteLock.WriteLock getIndexWriteLock(String name) {
        ReentrantReadWriteLock lock = this.indexLocks.get(name);
        if (lock == null) {
            throw new StateException("实体[" + this.clz.getName() + "]的索引属性名[" + name + "]无效");
        }
        return lock.writeLock();
    }


    public boolean hasIndexField(String name) {
        if (this.indexFields == null) {
            return false;
        }
        if (this.indexFields.containsKey(name)) {
            return true;
        }
        return false;
    }


    public String getUniqueQuery(String name) {
        if (this.uniques == null) {
            throw new StateException("实体[" + this.clz.getName() + "]的唯一属性名[" + name + "]无效");
        }
        Unique unique = this.uniques.get(name);
        if (unique == null) {
            throw new StateException("实体[" + this.clz.getName() + "]的唯一属性名[" + name + "]无效");
        }
        return unique.query();
    }


    public ReentrantReadWriteLock.ReadLock getUniqueReadLock(String name) {
        ReentrantReadWriteLock lock = this.uniqueLocks.get(name);
        if (lock == null) {
            throw new StateException("实体[" + this.clz.getName() + "]的唯一属性名[" + name + "]无效");
        }
        return lock.readLock();
    }


    public ReentrantReadWriteLock.WriteLock getUniqueWriteLock(String name) {
        ReentrantReadWriteLock lock = this.uniqueLocks.get(name);
        if (lock == null) {
            throw new StateException("实体[" + this.clz.getName() + "]的唯一属性名[" + name + "]无效");
        }
        return lock.writeLock();
    }


    public HashMap<String, DualHashBidiMap> buildUniqueCache() {
        HashMap<String, DualHashBidiMap> result = new HashMap<>(this.uniqueFields.size());
        for (String name : this.uniqueFields.keySet()) {
            DualHashBidiMap map = new DualHashBidiMap();
            result.put(name, map);
        }
        return result;
    }


    public Map<String, Object> getUniqueValues(IEntity entity) {
        if (this.uniqueFields == null) {
            throw new StateException("实体[" + this.clz.getName() + "]没有唯一属性配置无法获取唯一属性值");
        }

        try {
            Map<String, Object> result = new HashMap<>(this.uniqueFields.size());
            for (Map.Entry<String, Field> entry : this.uniqueFields.entrySet()) {
                Object value = ((Field) entry.getValue()).get(entity);
                result.put(entry.getKey(), value);
            }
            return result;
        } catch (Exception e) {
            throw new StateException("无法获取唯一属性值:" + e.getMessage());
        }
    }


    public boolean hasUniqueField() {
        if (this.uniqueFields == null) {
            return false;
        }
        return true;
    }


    public boolean cacheUnitIs(CacheUnit unit) {
        if (this.cached.unit() == unit) {
            return true;
        }
        return false;
    }


    public Class<? extends IEntity> getClz() {
        return this.clz;
    }


    public Cached getCached() {
        return this.cached;
    }


    public InitialConfig getInitialConfig() {
        return this.initialConfig;
    }


    public String getCacheName() {
        return this.clz.getSimpleName();
    }

    public int getCachedSize() {
        return this.cachedSize;
    }


    public String getPersisterName() {
        return this.cached.persister().value();
    }


    public int getExpireMinute() {
        return this.expireMinute;
    }
}