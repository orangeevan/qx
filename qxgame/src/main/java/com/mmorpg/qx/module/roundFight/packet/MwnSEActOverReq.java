package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 魔物娘技能效果表现完成
 * @since 16:48 2021/3/13
 */
@SocketPacket(packetId = PacketId.MWN_SKILL_EFFECT_ACT_OVER_REQ)
public class MwnSEActOverReq {

    @Protobuf(description = "魔物娘id")
    private long mwnId;

    public long getMwnId() {
        return mwnId;
    }

    public void setMwnId(long mwnId) {
        this.mwnId = mwnId;
    }
}
