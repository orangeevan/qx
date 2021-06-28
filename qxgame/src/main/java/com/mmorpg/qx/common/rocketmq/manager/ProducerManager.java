package com.mmorpg.qx.common.rocketmq.manager;

import com.haipaite.common.utility.RandomUtils;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.identify.manager.ServerConfigValue;
import com.mmorpg.qx.common.rocketmq.service.IProducerService;
import com.mmorpg.qx.common.rocketmq.test.TestMsg;
import com.mmorpg.qx.common.rocketmq.test.TestMsgBody;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author wang ke
 * @description: 消息生产者
 * @since 19:37 2021/3/15
 */
@Component
public class ProducerManager {
    private final Logger logger = LoggerFactory.getLogger(ProducerManager.class);

    private static DefaultMQProducer defaultMQProducer;

    @PostConstruct
    private void init() {
        start();
    }

    private void start() {
        try {
            // 参数信息
            String producerGroup = ServerConfigValue.getInstance().getRocketMqProducerGroup();
            String namesrvAddr = ServerConfigValue.getInstance().getRocketMqNameserver();
            boolean startServer = ServerConfigValue.getInstance().isRQProducerServer();
            if (!startServer) {
                return;
            }
            logger.info("DefaultMQProducer initialize! nameserver【{}】 producerGroup【{}】", namesrvAddr, producerGroup);
            // 初始化
            defaultMQProducer = new DefaultMQProducer(producerGroup);
            defaultMQProducer.setNamesrvAddr(namesrvAddr);
            defaultMQProducer.setInstanceName(String.valueOf(System.currentTimeMillis()));
            defaultMQProducer.start();
            testProducer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务停止
     */
    @PreDestroy
    public void destroy() {
        if (Objects.nonNull(defaultMQProducer)) {
            defaultMQProducer.shutdown();
            logger.info("DefaultMQProducer stop success!");
        }
    }

    public static MQProducer getMQProducer() {
        if (Objects.isNull(defaultMQProducer)) {
            throw new UnsupportedOperationException("服务器不能当消息生产者");
        }
        return defaultMQProducer;
    }

    private void testProducer() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            TestMsg testMsg = new TestMsg();
            TestMsgBody testMsgBody = new TestMsgBody();
            testMsgBody.setT1(1 + RandomUtils.nextInt());
            testMsgBody.setT2("test" + RandomUtils.nextInt());
            testMsg.setMsgBody(testMsgBody);
            testMsg.setGroup("test");
            testMsg.setTopic("testTopic");
            testMsg.setSubTag("TestMsgBody");
            testMsg.setArg(ServerConfigValue.getInstance().getRocketMqProducerGroup());
            boolean result = BeanService.getBean(IProducerService.class).sendMsg(ProducerManager.getMQProducer(), testMsg);
            if (result) {
                System.err.println("生产者生产消息 " + testMsg);
            }
        }, 30, 2, TimeUnit.SECONDS);
    }

    public static ProducerManager getInstance() {
        return BeanService.getBean(ProducerManager.class);
    }

}
