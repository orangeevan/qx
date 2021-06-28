package com.mmorpg.qx.common.rocketmq.model;

import com.mmorpg.qx.common.rocketmq.service.MQSocketMsgHandler;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.MessageQueue;

import java.io.IOException;

/**
 * @author wang ke
 * @description:
 * @since 11:38 2021/4/21
 */
public interface IMsg {
    /**
     * 消息主题
     *
     * @return
     */
    String getTopic();

    /**
     * 同一topic下，不同分类, 注意配置多个tag要用 ||(分隔符) 分隔
     *
     * @return
     */
    String getSubTag();

    /**
     * 消息分组
     *
     * @return
     */
    String getGroup();

    /**
     * 发送时参数，预用
     *
     * @return
     */
    Object arg();

    <B extends IMsgBody> B getMsgBody();

    /**
     * 消息主体发送前序列化
     *
     * @return
     */
    default byte[] encodeMsgBody() {
        try {
            return getMsgBody().encode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成消息体对象
     *
     * @param bytes
     * @return
     */
    default <T extends IMsg> T decodeMsgBody(byte[] bytes) throws IOException{
        return MQSocketMsgHandler.getInstance().decode(this.getClass(), bytes);
    }

    /**
     * 消息队列选择器，后期业务需要，部分消息必须在同队列处理
     *
     * @return
     */
    default MessageQueueSelector queueSelector() {
        return (mqs, msg, arg) -> {
            int hashCode = (msg.getTopic() + msg.getTags()).hashCode();
            if (arg() != null) {
                hashCode = arg().hashCode();
            }
            int index = hashCode % mqs.size();
            return mqs.get(index);
        };
    }
}
