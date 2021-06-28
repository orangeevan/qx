package com.mmorpg.qx.module.rank.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.rank.entity.RankKey;

/**
 * @author: yuanchengyan
 * @description:
 * @since 19:04 2021/4/7
 */
@SocketPacket(packetId = PacketId.RANKING_SELF_REQ)
public class RankingSelfReq {
    @Protobuf(description = "排行类型")
    private RankKey key;


    public RankKey getKey() {
        return key;
    }

    public void setKey(RankKey key) {
        this.key = key;
    }

}
