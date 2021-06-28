package com.mmorpg.qx.common.rocketmq.manager;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.identify.manager.ServerConfigValue;
import com.mmorpg.qx.common.rocketmq.service.IConsumerService;
import com.mmorpg.qx.common.rocketmq.service.impl.OrderConsumerServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wang ke
 * @description:
 * @since 19:48 2021/3/15
 */
@Component
public class ConsumerManager {
    private final Logger logger = LoggerFactory.getLogger(ConsumerManager.class);

    @Autowired
    private IConsumerService consumerService;

    private DefaultMQPushConsumer defaultMQPushConsumer;

    @PostConstruct
    private void init() {
        start();
    }

    private void start() {
        try {
            String naderAdd = ServerConfigValue.getInstance().getRocketMqNameserver();
            String consumerGroup = ServerConfigValue.getInstance().getRocketMqConsumerGroup();
            String topic = ServerConfigValue.getInstance().getRocketMqTopic();
            String topicTag = ServerConfigValue.getInstance().getRocketMqTopicTag();
            boolean startServer = ServerConfigValue.getInstance().isRQConsumerServer();
            if (!startServer) {
                return;
            }
            // 参数信息
            logger.info("DefaultMQPushConsumer initialize! nameserver【{}】 consumerGroup【{}】", naderAdd, consumerGroup);
            // 一个应用创建一个Consumer
            defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
            defaultMQPushConsumer.setNamesrvAddr(naderAdd);
            defaultMQPushConsumer.setInstanceName(String.valueOf(System.currentTimeMillis()));
            // 订阅指定topic跟tag
            defaultMQPushConsumer.subscribe(topic, topicTag);
            // 设置Consumer第一次启动是从队列头部开始消费
            // 如果非第一次启动，那么按照上次消费的位置继续消费
            defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            // 设置为集群消费
            //defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
            defaultMQPushConsumer.registerMessageListener(((OrderConsumerServiceImpl) consumerService).consumeRule());
            //设置队列策略
            defaultMQPushConsumer.setAllocateMessageQueueStrategy(new MessageQueueStrategy());
            defaultMQPushConsumer.start();
            logger.info("DefaultMQPushConsumer start success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() {
        if (Objects.nonNull(defaultMQPushConsumer)) {
            defaultMQPushConsumer.shutdown();
            logger.info("DefaultMQPushConsumer stop success!");
        }
    }

    public static ConsumerManager getInstance() {
        return BeanService.getBean(ConsumerManager.class);
    }

    private static class MessageQueueStrategy implements AllocateMessageQueueStrategy {

        @Override
        public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll, List<String> cidAll) {
            int hashCode = consumerGroup.hashCode();
            int queueIndex = hashCode % mqAll.size();
            List<MessageQueue> subQueue = new ArrayList<>();
            subQueue.add(mqAll.get(queueIndex));
            return subQueue;
        }

        @Override
        public String getName() {
            return "IP消费分组";
        }
    }
}
