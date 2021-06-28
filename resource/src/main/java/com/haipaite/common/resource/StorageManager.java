package com.haipaite.common.resource;

import com.haipaite.common.resource.other.ResourceDefinition;
import com.haipaite.common.utility.JsonUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;


public class StorageManager implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(StorageManager.class);

    private ConcurrentHashMap<Class, ResourceDefinition> definitions = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Class<?>, Storage<?, ?>> storages = new ConcurrentHashMap<>();


    private static ConcurrentHashMap<String, InputStream> caches = new ConcurrentHashMap<>();


    private ApplicationContext applicationContext;


    public void initialize(ResourceDefinition definition) {
        Class<?> clz = definition.getClz();
        if (this.definitions.putIfAbsent(clz, definition) != null) {
            ResourceDefinition prev = this.definitions.get(clz);
            FormattingTuple message = MessageFormatter.format("类[{}]的资源定义[{}]已经存在", clz, prev);
            logger.error(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }
        initializeStorage(clz);
    }


    public void reload(Class<?> clz, String path) {
        ResourceDefinition definition = this.definitions.get(clz);
        if (definition == null) {
            FormattingTuple message = MessageFormatter.format("类[{}]的资源定义不存在", clz);
            logger.error(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }

        Storage<?, ?> storage = getStorage(clz);
        storage.reload(path);
    }


    public <T> T getResource(Object key, Class<T> clz) {
        Storage<?, ?> storage = getStorage(clz);
        return (T) storage.get(key, false);
    }


    public Storage<?, ?> getStorage(Class clz) {
        if (this.storages.containsKey(clz)) {
            return this.storages.get(clz);
        }
        return initializeStorage(clz);
    }


    public Storage<?, ?>[] listStorages() {
        List<Storage<?, ?>> storages = new ArrayList<>();
        for (Storage<?, ?> storage : this.storages.values()) {
            storages.add(storage);
        }
        return storages.<Storage<?, ?>>toArray((Storage<?, ?>[]) new Storage[0]);
    }

    private Storage initializeStorage(Class<?> clz) {
        ResourceDefinition definition = this.definitions.get(clz);
        if (definition == null) {
            FormattingTuple message = MessageFormatter.format("静态资源[{}]的信息定义不存在，可能是配置缺失", clz.getSimpleName());
            logger.error(message.getMessage());
            throw new IllegalStateException(message.getMessage());
        }
        AutowireCapableBeanFactory beanFactory = this.applicationContext.getAutowireCapableBeanFactory();
        Storage<?, ?> storage = beanFactory.createBean(Storage.class);

        Storage prev = this.storages.putIfAbsent(clz, storage);
        if (prev == null) {
            storage.initialize(definition);
        }
        return (prev == null) ? storage : prev;
    }

    public void writeJson(String path) throws Exception {
        for (Map.Entry<Class<?>, Storage<?, ?>> entry : this.storages.entrySet()) {
            try {
                long startTime = System.currentTimeMillis();
                String json = JsonUtils.object2String(((Storage) entry.getValue()).getAll());

                File file = new File(String.format(path + "\\%s.json", new Object[]{((Class) entry.getKey()).getSimpleName()}));
                createFile(file);
                FileWriter fw = new FileWriter(file);
                fw.write(json);
                fw.flush();
                fw.close();

                long endTime = System.currentTimeMillis();
                System.out.println("处理完成" + ((Class) entry.getKey()).getSimpleName() + " 耗时：" + (endTime - startTime) + " ms");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(((Class) entry.getKey()).getSimpleName() + " 有问题！");
            }
        }
    }

    private boolean createFile(File file) throws Exception {
        if (!file.exists()) {
            mkdir(file.getParentFile());
        }
        return file.createNewFile();
    }

    private void mkdir(File file) {
        if (!file.getParentFile().exists()) {
            mkdir(file.getParentFile());
        }
        file.mkdir();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public static void putCache(String key, InputStream inputStream) {
        caches.put(key, inputStream);
    }

    public static InputStream getFromCache(String key) {
        return caches.get(key);
    }

    public ConcurrentHashMap<Class, ResourceDefinition> getDefinitions() {
        return this.definitions;
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\StorageManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */