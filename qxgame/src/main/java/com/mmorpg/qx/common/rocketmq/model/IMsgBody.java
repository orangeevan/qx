package com.mmorpg.qx.common.rocketmq.model;

import com.mmorpg.qx.common.rocketmq.service.MQSocketMsgHandler;

import java.io.IOException;

/**
 * @author wang ke
 * @description: 消息体接口
 * @since 15:01 2021/6/10
 */
public interface IMsgBody {
    default byte[] encode() throws IOException {
        return MQSocketMsgHandler.getInstance().encode(this);
    }
}
