package com.mmorpg.qx.module.rank.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.rank.entity.RankKey;
import com.mmorpg.qx.module.rank.packet.vo.RankingPlayerLevelItemVo;

import java.util.List;

/**
 * @author: yuanchengyan
 * @description:不能和个人排行信息一起请求（会出现排序不一致的可能性）
 * @since 19:04 2021/4/7
 */
@SocketPacket(packetId = PacketId.RANKING_LIST_RESP)
public class RankingListResp {
    @Protobuf(description = "排行类型")
    private RankKey key;
    /**
     * 所有排行榜类型列表往下加，虽然违反开闭原则，但减少协议数量
     */
    @Protobuf(description = "玩家等级排行项")
    private List<RankingPlayerLevelItemVo> playerLevelItemVos;

    public static RankingListResp valueOf() {
        RankingListResp resp = new RankingListResp();
        return resp;
    }

    public RankKey getKey() {
        return key;
    }

    public void setKey(RankKey key) {
        this.key = key;
    }

    public List<RankingPlayerLevelItemVo> getPlayerLevelItemVos() {
        return playerLevelItemVos;
    }

    public void setPlayerLevelItemVos(List<RankingPlayerLevelItemVo> playerLevelItemVos) {
        this.playerLevelItemVos = playerLevelItemVos;
    }
}

