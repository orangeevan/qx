package com.mmorpg.qx.module.rank.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.rank.model.value.impl.PlayerLevelRankingValue;

/**
 * @author: yuanchengyan
 * @description:
 * @since 19:05 2021/4/7
 */
public class RankingPlayerLevelItemVo implements IRankingItemVo {
    @Protobuf(description = "玩家id")
    private long playerId;
    @Protobuf(description = "玩家名")
    private String playerName;
    @Protobuf(description = "排名信息")
    private PlayerLevelRankingValue val;

    public static RankingPlayerLevelItemVo valueOf(long playerId, String playerName, PlayerLevelRankingValue val) {
        RankingPlayerLevelItemVo vo = new RankingPlayerLevelItemVo();
        vo.playerId = playerId;
        vo.playerName = playerName;
        vo.val = val;
        return vo;
    }
}

