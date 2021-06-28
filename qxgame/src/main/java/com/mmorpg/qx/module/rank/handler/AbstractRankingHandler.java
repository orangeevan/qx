package com.mmorpg.qx.module.rank.handler;

import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.rank.enums.RankSubType;
import com.mmorpg.qx.module.rank.enums.RankType;
import com.mmorpg.qx.module.rank.model.IRankingObject;
import com.mmorpg.qx.module.rank.model.value.IRankingValue;

/**
 * @author: yuanchengyan
 * @description:
 * @since 20:58 2021/4/7
 */
public abstract class AbstractRankingHandler<PK, T extends IRankingObject, V extends IRankingValue> {

    public abstract void sentRankingListResp(Player player, RankType type, RankSubType subType);

    public abstract void sentRankingSelfResp(Player player, RankType type, RankSubType subType);

    public abstract T createEmptyRankingObject(PK pk, V v);

}

