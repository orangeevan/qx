package com.mmorpg.qx.module.rank.handler.impl;

import com.haipaite.common.utility.ObjectUtils;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.rank.enums.RankSubType;
import com.mmorpg.qx.module.rank.enums.RankType;
import com.mmorpg.qx.module.rank.handler.AbstractRankingHandler;
import com.mmorpg.qx.module.rank.manager.RankManager;
import com.mmorpg.qx.module.rank.model.CommRankData;
import com.mmorpg.qx.module.rank.model.rankimpl.LevelRankingObject;
import com.mmorpg.qx.module.rank.model.value.impl.PlayerLevelRankingValue;
import com.mmorpg.qx.module.rank.packet.RankingListResp;
import com.mmorpg.qx.module.rank.packet.RankingSelfResp;
import com.mmorpg.qx.module.rank.packet.vo.RankingPlayerLevelItemVo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: yuanchengyan
 * @description:
 * @since 20:59 2021/4/7
 */
public class PlayerLevelRankingHandler extends AbstractRankingHandler<Long, LevelRankingObject, PlayerLevelRankingValue> {

    @Override
    public void sentRankingListResp(Player player, RankType type, RankSubType subType) {
        CommRankData commRankData = RankManager.getInstance().getCommRankData(type, subType);
        List<LevelRankingObject> rankingObjects = commRankData.getCacheOrderRD();
        List<RankingPlayerLevelItemVo> vos = rankingObjects.stream().map(obj -> obj.convert()).collect(Collectors.toList());
        RankingListResp resp = RankingListResp.valueOf();
        resp.setKey(commRankData.getKey());
        resp.setPlayerLevelItemVos(vos);
        PacketSendUtility.sendPacket(player, resp);
    }

    @Override
    public void sentRankingSelfResp(Player player, RankType type, RankSubType subType) {
        CommRankData commRankData = RankManager.getInstance().getCommRankData(type, subType);
        LevelRankingObject rankingObject = (LevelRankingObject) commRankData.getRankingObject(player.getObjectId());
        if (ObjectUtils.isEmpty(rankingObject)) {
            int level = player.getPlayerEnt().getLevel();
            PlayerLevelRankingValue value = PlayerLevelRankingValue.valueOf(level);
            rankingObject = createEmptyRankingObject(player.getObjectId(), value);
        }
        RankingSelfResp resp = RankingSelfResp.valueOf();
        resp.setKey(commRankData.getKey());
        resp.setPlayerLevelItemVo(rankingObject.convert());
        PacketSendUtility.sendPacket(player, resp);

    }

    @Override
    public LevelRankingObject createEmptyRankingObject(Long pk, PlayerLevelRankingValue rankingValue) {
        return LevelRankingObject.valueOf(pk, rankingValue);
    }

}

