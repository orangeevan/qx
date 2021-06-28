package com.mmorpg.qx.common.rocketmq.service;

import com.mmorpg.qx.common.rocketmq.model.IMsg;
import org.apache.rocketmq.client.producer.MQProducer;

/**
 * @author wang ke
 * @description:
 * @since 11:21 2021/4/21
 */
public interface IProducerService {
    /**
     * 发送消息
     * @param producer
     * @param msg
     * @param <T>
     * @return
     */
    <T extends IMsg> boolean sendMsg(MQProducer producer, T msg);
}
