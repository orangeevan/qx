package com.mmorpg.qx.common.rocketmq.service.impl;

import com.mmorpg.qx.common.rocketmq.model.IMsg;
import com.mmorpg.qx.common.rocketmq.service.IProducerService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Objects;

/**
 * @author wang ke
 * @description:
 * @since 17:06 2021/4/21
 */
@Component
public class ProducerServiceImpl implements IProducerService {

    private final Logger logger = LoggerFactory.getLogger(ProducerServiceImpl.class);

    @Override
    public <T extends IMsg> boolean sendMsg(MQProducer producer, T imsg) {
        try {
            Message msg = new Message(imsg.getTopic(), imsg.getSubTag(), imsg.encodeMsgBody());
            SendResult sendResult = producer.send(msg, imsg.queueSelector(), imsg.arg());
            if (Objects.isNull(sendResult) || sendResult.getSendStatus() != SendStatus.SEND_OK) {
                return false;
            }
            return true;
        } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
