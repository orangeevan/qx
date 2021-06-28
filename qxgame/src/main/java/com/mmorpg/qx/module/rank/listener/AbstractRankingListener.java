package com.mmorpg.qx.module.rank.listener;

import com.mmorpg.qx.module.rank.enums.RankSubType;
import com.mmorpg.qx.module.rank.enums.RankType;
import com.mmorpg.qx.module.rank.manager.RankManager;
import com.mmorpg.qx.module.rank.model.IRankingObject;
import com.mmorpg.qx.module.rank.resource.RankResource;

import java.util.List;

/**
 * @author: yuanchengyan
 * @description:
 * @since 20:59 2021/4/7
 */
public abstract class AbstractRankingListener {

    public abstract RankType rankType();

    public void commit(RankType type, IRankingObject rankingObject) {
        RankResource rankResource = RankManager.getInstance().getRankResource(type);
        List<RankSubType> subTypes = rankResource.getRankSubTypes();
        for (RankSubType subType : subTypes) {
            RankManager.getInstance().commit(type, subType, rankingObject);
        }
    }
}

