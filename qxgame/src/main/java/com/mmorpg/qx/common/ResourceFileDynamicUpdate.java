package com.mmorpg.qx.common;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.StorageManager;
import com.mmorpg.qx.common.identify.manager.ServerConfigValue;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 资源动态更新，动态更新热更Manager需继承{@link ResourceReload}
 *
 * @author wangke
 * @since v1.0 2019/4/9
 */
@Component
public class ResourceFileDynamicUpdate extends FileAlterationListenerAdaptor implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = SysLoggerFactory.getLogger(ResourceFileDynamicUpdate.class);

    private ApplicationContext context;

    private FileAlterationMonitor monitor;

    private final static AtomicBoolean START = new AtomicBoolean(false);
    public final static String EXCEL_FILE = ".xlsx";
    public final static String CLASS_FILE = ".class";
    private ResourceFileDynamicUpdate self;

    @PostConstruct
    void init() {
        self = this;
    }

    @Override
    public void onFileChange(File file) {
        if (file.isDirectory()) {
            return;
        }

        //excel动态更新文件
        if (file.getName().endsWith(EXCEL_FILE)) {
            try {
                StorageManager storageManager = this.context.getBean(StorageManager.class);
                storageManager.getDefinitions().values().forEach(resourceDefinition -> {
                    if (resourceDefinition.getLocation().contains(file.getName())) {
                        logger.info("资源文件[{}]改变", file.getName());
                        storageManager.reload(resourceDefinition.getClz(), file.getAbsolutePath());
                    }
                });
            } catch (Exception e) {
                logger.error("Excel静态资源热更异常 {}", e);
            }
        }

        //class热更
        if (file.getName().endsWith(CLASS_FILE)) {
            try {
                String className = file.getPath();
                String resourceDir = ServerConfigValue.getInstance().getHotUpdateDir();
                resourceDir = resourceDir.replace("\\", "/");
                if (!resourceDir.endsWith("/")) {
                    resourceDir += "/";
                }
                className = className.replace("\\", "/").replace(resourceDir, "").replace("/", ".");
                JavaAgent.javaAgent(resourceDir, new String[]{className});
                logger.info("类文件 [{}] 热更成功,目录层次 [{}]", className, resourceDir);
            } catch (Exception e) {
                logger.error("热更异常 {}", e);
            }
        }
    }

    @Override
    public void onFileCreate(File file) {
        onFileChange(file);
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (START.compareAndSet(false, true)) {
            start();
        }
    }

    private void start() {
        try {
            String directory = ServerConfigValue.getInstance().getHotUpdateDir();
            FileAlterationObserver observer = new FileAlterationObserver(directory);
            //设置文件变化监听器
            observer.addListener(this);
            monitor = new FileAlterationMonitor(10000, observer);
            monitor.start();
            logger.info("动态更新监听已经启动: {}", observer.getDirectory().getAbsolutePath());
        } catch (Exception e) {
            logger.error("动态更新监听启动异常", e);
        }


    }

    @PreDestroy
    private void stop() {
        try {
            if (monitor != null) {
                logger.info("动态更新监听线程关闭");
                monitor.stop();
            }
        } catch (Exception e) {
            logger.error("动态更新监听结束异常", e);
        } finally {
            START.compareAndSet(true, false);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
