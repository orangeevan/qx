package com.mmorpg.qx.common.identify.manager;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加载server.propertise文件.支持热加载
 *
 * @author wang ke
 * @since v1.0 2018年3月13日
 */
@Component
public class ServerConfigValue implements ApplicationContextAware {

    private static ServerConfigValue self;

    public static ServerConfigValue getInstance() {
        return self;
    }

    @PostConstruct
    public void init() throws IOException {
        self = this;
        reload();
    }

    public void reload() throws IOException {
        Resource resource = applicationContext.getResource("server.properties");
        if (resource.isReadable()) {
            Properties props = new Properties();
            props.load(resource.getInputStream());
            for (Entry<Object, Object> entry : props.entrySet()) {
                propertise.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
    }

    private ConcurrentHashMap<String, String> propertise = new ConcurrentHashMap<>();

    public int getServerId() {
        return Integer.valueOf(propertise.get("server.config.serverid"));
    }


    public String getServerMsgToken() {
        return propertise.get("server.config.msg_token");
    }


    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /***
     * 地图配置路径
     * @return
     */
    public String getMapResourcePath() {
        return propertise.get("server.config.mapResourcePath");
    }

    /***
     * 地图布怪配置路径
     * @return
     */
    public String getSpawnResourcePath() {
        return propertise.get("server.config.spawnResourcePath");
    }

    public String getPacketPath() {
        return propertise.get("server.config.packetPath");
    }

    public String getHotUpdateDir() {
        return propertise.get("server.config.hotUpdate");
    }

    public String getRedisUrl() {
        return propertise.get("server.redis.cluster");
    }

    public int getRedisPoolMinIdle() {
        return Integer.valueOf(propertise.get("server.redis.pool.minIdle"));
    }

    public int getRedisPoolMaxTotal() {
        return Integer.valueOf(propertise.get("server.redis.pool.maxTotal"));
    }

    public int getRedisPoolMaxIdle() {
        return Integer.valueOf(propertise.get("server.redis.pool.maxIdle"));
    }

    public String getRocketMqNameserver() {
        return propertise.get("server.rocketmq.nameserver");
    }

    public String getRocketMqConsumerGroup() {
        return propertise.get("server.rocketmq.consumerGroup");
    }

    public String getRocketMqProducerGroup() {
        return propertise.get("server.rocketmq.producerGroup");
    }

    public String getRocketMqTopic() {
        return propertise.get("server.rocketmq.topic");
    }

    public String getRocketMqTopicTag() {
        return propertise.get("server.rocketmq.topiclTag");
    }

    public boolean isRQConsumerServer() {
        return Integer.valueOf(propertise.get("server.rocketmq.consumerServer")) > 0;
    }

    public boolean isRQProducerServer() {
        return Integer.valueOf(propertise.get("server.rocketmq.producerServer")) > 0;
    }
}
