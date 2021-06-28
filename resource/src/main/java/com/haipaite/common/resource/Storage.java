package com.haipaite.common.resource;

import com.haipaite.common.resource.other.*;
import com.haipaite.common.resource.reader.ReaderHolder;
import com.haipaite.common.resource.reader.ResourceReader;
import com.haipaite.common.utility.JsonUtils;
import com.haipaite.common.utility.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;

public class Storage<K, V> extends Observable implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);


    @Autowired
    private ReaderHolder readerHolder;


    private boolean initialized;


    private ResourceDefinition resourceDefinition;


    private ResourceReader reader;

    private Getter identifier;

    private Map<String, IndexGetter> indexGetters;

    public synchronized void initialize(ResourceDefinition definition) {
        if (this.initialized) {
            return;
        }

        this.initialized = true;

        this.resourceDefinition = definition;
        this.reader = this.readerHolder.getReader(definition.getFormat());
        this.identifier = GetterBuilder.createIdGetter(definition.getClz());
        this.indexGetters = GetterBuilder.createIndexGetters(definition.getClz());

        Set<InjectDefinition> injects = definition.getStaticInjects();
        for (InjectDefinition inject : injects) {
            Field field = inject.getField();
            Object injectValue = inject.getValue(this.applicationContext);
            try {
                field.set(null, injectValue);
            } catch (Exception e) {
                FormattingTuple message = MessageFormatter.format("无法注入静态资源[{}]的[{}]属性值", definition.getClz().getName(), inject
                        .getField().getName());
                logger.error(message.getMessage());
                throw new IllegalStateException(message.getMessage());
            }
        }

        reload((String) null);
    }


    private Map<K, V> values = new HashMap<>();


    private Map<String, Map<Object, List<V>>> indexs = new HashMap<>();


    private Map<String, Map<Object, V>> uniques = new HashMap<>();


    private final Lock readLock;


    private final Lock writeLock;

    private ApplicationContext applicationContext;


    public Storage() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }


    public V get(Object key, boolean flag) {
        isReady();
        this.readLock.lock();
        try {
            V result = this.values.get(key);
            if (flag && result == null) {
                FormattingTuple message = MessageFormatter.format("标识为[{}]的静态资源[{}]不存在", key, getClz().getName());
                logger.error(message.getMessage());
                throw new IllegalStateException(message.getMessage());
            }
            return result;
        } finally {
            this.readLock.unlock();
        }
    }


    public boolean containsId(K key) {
        isReady();
        this.readLock.lock();
        try {
            return this.values.containsKey(key);
        } finally {
            this.readLock.unlock();
        }
    }


    public Collection<V> getAll() {
        isReady();
        this.readLock.lock();
        try {
            return (Collection) Collections.unmodifiableCollection(this.values.values());
        } finally {
            this.readLock.unlock();
        }
    }


    public V getUnique(String name, Object value) {
        isReady();
        this.readLock.lock();
        try {
            Map<Object, V> index = this.uniques.get(name);
            if (index == null) {
                return null;
            }
            return index.get(value);
        } finally {
            this.readLock.unlock();
        }
    }


    public List<V> getIndex(String name, Object value) {
        isReady();
        this.readLock.lock();
        try {
            Map<Object, List<V>> index = this.indexs.get(name);
            if (index == null) {
                return Collections.EMPTY_LIST;
            }
            List<V> indexList = index.get(value);
            if (indexList == null) {
                return Collections.EMPTY_LIST;
            }
            ArrayList<V> result = new ArrayList<>(indexList);
            return result;
        } finally {
            this.readLock.unlock();
        }
    }


    public void reload(String location) {
        isReady();
        this.writeLock.lock();
        InputStream input = null;

        try {
            if (this.resourceDefinition.getCacheKey() != null) {
                InputStream inputStream = StorageManager.getFromCache(this.resourceDefinition.getCacheKey());
                if (inputStream != null) {
                    input = inputStream;
                } else {
                    FormattingTuple message = MessageFormatter.format("资源[{}]ERROR102", "resource");
                    logger.error(message.getMessage());
                    throw new IllegalStateException(message.getMessage());
                }

            } else if (StringUtils.isBlank(location)) {
                Resource resource = this.applicationContext.getResource(getLocation());
                logger.info("配置资源resource路径：[{}]", resource.getURL().getPath());
                input = resource.getInputStream();
            } else {
                input = new FileInputStream(new File(location));
            }


            List<V> list = this.reader.read(input, getClz());
            Iterator<V> it = list.iterator();
            clear();
            while (it.hasNext()) {
                V obj = it.next();

                Set<InjectDefinition> injects = this.resourceDefinition.getInjects();
                for (InjectDefinition inject : injects) {
                    Field field = inject.getField();
                    Object value = inject.getValue(this.applicationContext);
                    try {
                        field.set(obj, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (put(obj) != null) {
                    FormattingTuple message = MessageFormatter.format("[{}]资源[{}]的唯一标识重复", getClz(),
                            JsonUtils.object2String(obj));
                    logger.error(message.getMessage());
                    throw new IllegalStateException(message.getMessage());
                }
            }

            for (Map.Entry<String, Map<Object, List<V>>> entry : this.indexs.entrySet()) {
                String key = entry.getKey();
                IndexGetter getter = this.indexGetters.get(key);
                if (getter.hasComparator()) {
                    for (List<V> values : (Iterable<List<V>>) ((Map) entry.getValue()).values()) {
                        Collections.sort(values, getter.getComparator());
                    }
                }
            }

            setChanged();
            notifyObservers();
            logger.info("加载资源，路径信息 [{}]", this.resourceDefinition.getLocation());
        } catch (IOException e) {
            FormattingTuple message = MessageFormatter.format("静态资源[{}]所对应的资源文件[{}]不存在", getClz().getName(),
                    getLocation());
            logger.error(message.getMessage());
            throw new IllegalStateException(message.getMessage());
        } catch (ClassCastException e) {
            FormattingTuple message = MessageFormatter.format("静态资源[{}]配置的索引内容排序器不正确", getClz().getName(), e);
            logger.error(message.getMessage());
            throw new IllegalStateException(message.getMessage(), e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                logger.error("配置资源读取错误", e);
            }
            this.writeLock.unlock();
        }
    }


    public boolean isInitialized() {
        return this.initialized;
    }


    public String getLocation() {
        return this.resourceDefinition.getLocation();
    }


    private void isReady() {
        if (!isInitialized()) {
            String message = "未初始化完成";
            logger.error(message);
            throw new RuntimeException(message);
        }
    }


    private void clear() {
        this.values.clear();
        this.indexs.clear();
        this.uniques.clear();
    }


    private V put(V value) {
        K key = (K) this.identifier.getValue(value);
        if (key == null) {
            FormattingTuple message = MessageFormatter.format("静态资源[{}]存在标识属性为null的配置项", getClz().getName());
            logger.error(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }
        V result = this.values.put(key, value);


        for (IndexGetter getter : this.indexGetters.values()) {
            String name = getter.getName();
            Object indexKey = getter.getValue(value);


            if (getter.isUnique()) {
                Map<Object, V> map = loadUniqueIndex(name);
                if (map.put(indexKey, value) != null) {
                    FormattingTuple message = MessageFormatter.format("[{}]资源的唯一索引[{}]的值[{}]重复", new Object[]{
                            getClz().getName(), name, indexKey});
                    logger.debug(message.getMessage());
                    throw new RuntimeException(message.getMessage());
                }
                continue;
            }
            List<V> index = loadListIndex(name, indexKey);
            index.add(value);
        }


        return result;
    }

    private List<V> loadListIndex(String name, Object key) {
        Map<Object, List<V>> index = loadListIndex(name);
        if (index.containsKey(key)) {
            return index.get(key);
        }

        List<V> result = new ArrayList<>();
        index.put(key, result);
        return result;
    }

    private Map<Object, List<V>> loadListIndex(String name) {
        if (this.indexs.containsKey(name)) {
            return this.indexs.get(name);
        }

        Map<Object, List<V>> result = new HashMap<>();
        this.indexs.put(name, result);
        return result;
    }

    private Map<Object, V> loadUniqueIndex(String name) {
        if (this.uniques.containsKey(name)) {
            return this.uniques.get(name);
        }

        Map<Object, V> result = new HashMap<>();
        this.uniques.put(name, result);
        return result;
    }

    public Class<V> getClz() {
        return this.resourceDefinition.getClz();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public ResourceDefinition getResourceDefinition() {
        return this.resourceDefinition;
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\Storage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */