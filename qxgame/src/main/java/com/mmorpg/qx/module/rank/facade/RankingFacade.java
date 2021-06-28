package com.mmorpg.qx.module.rank.facade;

import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.rank.packet.RankingListReq;
import com.mmorpg.qx.module.rank.packet.RankingSelfReq;
import com.mmorpg.qx.module.rank.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: yuanchengyan
 * @description:
 * @since 10:53 2021/4/8
 */
@Component
public class RankingFacade {
    @Autowired
    private RankService rankService;
    @Autowired
    private PlayerManager playerManager;

    public void reqRankingList(Wsession session, RankingListReq req) {
        Player player = playerManager.getPlayerBySession(session);
        rankService.reqRankingList(player, req.getKey());
    }

    public void reqRankingSelf(Wsession session, RankingSelfReq req) {
        Player player = playerManager.getPlayerBySession(session);
        rankService.reqRankingSelf(player, req.getKey());
    }
}

