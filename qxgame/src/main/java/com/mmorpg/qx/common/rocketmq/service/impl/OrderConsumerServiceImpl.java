package com.mmorpg.qx.common.rocketmq.service.impl;

import com.mmorpg.qx.common.rocketmq.service.IConsumerService;
import com.mmorpg.qx.common.rocketmq.service.MQSocketMsgHandler;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wang ke
 * @description: 游戏内所有消息有序
 * @since 17:01 2021/4/21
 */
@Component
public class OrderConsumerServiceImpl implements IConsumerService {
    private final Logger logger = LoggerFactory.getLogger(OrderConsumerServiceImpl.class);

    @Autowired
    private MQSocketMsgHandler msgHandler;

    @Override
    public MessageListenerOrderly consumeRule() {
        return (list, consumeOrderlyContext) -> consumeMsg(list) ? ConsumeOrderlyStatus.SUCCESS : ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
    }

    @Override
    public boolean consumeMsg(List<MessageExt> list) {
        list.stream().forEach(messageExt -> msgHandler.handleMsg(messageExt));
        System.err.println("消费者消费："+list);
        logger.info("收到消息[{}]", list);
        return true;
    }
}
