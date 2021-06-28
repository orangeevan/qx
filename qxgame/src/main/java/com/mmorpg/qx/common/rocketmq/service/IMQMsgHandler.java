package com.mmorpg.qx.common.rocketmq.service;

import com.mmorpg.qx.common.rocketmq.model.IMsgBody;
import org.apache.rocketmq.common.message.MessageExt;

import java.io.IOException;

/**
 * @author wang ke
 * @description:
 * @since 20:54 2021/6/10
 */
public interface IMQMsgHandler {

    void handleMsg(MessageExt messageExt);

    byte[] encode(IMsgBody body) throws IOException;

    <M extends IMsgBody> M decode(Class mClass, byte[] bytes) throws IOException;
}
