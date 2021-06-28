package com.mmorpg.qx.module.rank.model.rankimpl;

import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.packet.vo.PlayerShortInfo;
import com.mmorpg.qx.module.rank.model.IRankingObject;
import com.mmorpg.qx.module.rank.model.value.impl.PlayerLevelRankingValue;
import com.mmorpg.qx.module.rank.packet.vo.RankingPlayerLevelItemVo;

/**
 * @author wang ke
 * @description:
 * @since 16:48 2021/3/11
 */
public class LevelRankingObject extends IRankingObject<Long, PlayerLevelRankingValue> {

    public static LevelRankingObject valueOf(Long id, PlayerLevelRankingValue value) {
        LevelRankingObject rankingObject = new LevelRankingObject();
        rankingObject.setId(id);
        rankingObject.setValue(value);
        return rankingObject;
    }

    @Override
    public RankingPlayerLevelItemVo convert() {
        PlayerShortInfo playerShortInfo = PlayerManager.getInstance().getPlayerShortInfo(id);
        String playerName = playerShortInfo.getName();
        return RankingPlayerLevelItemVo.valueOf(id, playerName, value);
    }
}
