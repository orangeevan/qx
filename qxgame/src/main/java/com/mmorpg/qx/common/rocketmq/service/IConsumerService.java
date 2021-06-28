package com.mmorpg.qx.common.rocketmq.service;

import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author wang ke
 * @description: 消息消费者
 * @since 11:22 2021/4/21
 */
public interface IConsumerService {

    /**
     * 消息消费规则,一般情况下都是有序消费
     *
     * @return
     */
    <T extends MessageListener> T consumeRule();


    /**
     * 消费消息
     *
     * @param list
     * @return
     */
    boolean consumeMsg(List<MessageExt> list);
}
