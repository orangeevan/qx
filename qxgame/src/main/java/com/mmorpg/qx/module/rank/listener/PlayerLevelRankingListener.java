package com.mmorpg.qx.module.rank.listener;

import com.haipaite.common.event.anno.ReceiverAnno;
import com.mmorpg.qx.module.player.event.PlayerLevelUpEvent;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.rank.enums.RankType;
import com.mmorpg.qx.module.rank.model.rankimpl.LevelRankingObject;
import com.mmorpg.qx.module.rank.model.value.impl.PlayerLevelRankingValue;
import org.springframework.stereotype.Component;

/**
 * @author: yuanchengyan
 * @description:
 * @since 20:59 2021/4/7
 */
@Component
public class PlayerLevelRankingListener extends AbstractRankingListener {
    @Override
    public RankType rankType() {
        return RankType.PlayerLevel;
    }

    @ReceiverAnno
    public void onPlayerLevelUp(PlayerLevelUpEvent event) {
        Player player = event.getPlayer();
        int level = player.getPlayerEnt().getLevel();
        PlayerLevelRankingValue value = PlayerLevelRankingValue.valueOf(level);
        LevelRankingObject obj = LevelRankingObject.valueOf(player.getObjectId(), value);
        commit(rankType(), obj);
    }
}

