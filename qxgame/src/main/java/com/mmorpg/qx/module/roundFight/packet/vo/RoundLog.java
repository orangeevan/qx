package com.mmorpg.qx.module.roundFight.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import java.io.Serializable;

/**
 * @author wang ke
 * @description: 回合日志，玩家离线后发生的战斗记录
 * @since 14:36 2021/1/6
 */
public class RoundLog implements Serializable {
    @Protobuf(description = "消息id")
    private short msgId;

    @Protobuf(description = "消息体，根据具体消息Id解析对象消息结构体")
    private Object msgBody;

    public static RoundLog valueOf(short msgId, Object msgBody){
        RoundLog roundLog = new RoundLog();
        roundLog.msgId = msgId;
        roundLog.msgBody = msgBody;
        return roundLog;
    }

    public short getMsgId() {
        return msgId;
    }

    public void setMsgId(short msgId) {
        this.msgId = msgId;
    }

    public Object getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(Object msgBody) {
        this.msgBody = msgBody;
    }
}
