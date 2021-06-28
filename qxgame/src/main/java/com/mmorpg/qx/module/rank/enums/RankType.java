package com.mmorpg.qx.module.rank.enums;

import com.haipaite.common.utility.ObjectUtils;
import com.mmorpg.qx.module.rank.entity.RankKey;
import com.mmorpg.qx.module.rank.handler.AbstractRankingHandler;
import com.mmorpg.qx.module.rank.handler.impl.PlayerLevelRankingHandler;
import com.mmorpg.qx.module.rank.manager.RankManager;
import com.mmorpg.qx.module.rank.model.CommRankData;
import com.mmorpg.qx.module.rank.model.IRankingObject;
import com.mmorpg.qx.module.rank.model.rankimpl.LevelRankingObject;
import com.mmorpg.qx.module.rank.model.value.IRankingValue;
import com.mmorpg.qx.module.rank.model.value.impl.PlayerLevelRankingValue;

/**
 * @author wang ke
 * @description:
 * @since 11:56 2021/3/11
 */
public enum RankType {
    PlayerLevel(1, "玩家等级榜", true, 10000, new PlayerLevelRankingHandler()) {
        @Override
        public CommRankData<Long, LevelRankingObject> createCommRankData(RankSubType subType) {
            return new CommRankData<Long, LevelRankingObject>(RankKey.valueOf(this, subType), this.getRankPeriod());
        }

        @Override
        public Class<LevelRankingObject> getTypeClass() {
            return LevelRankingObject.class;
        }

        @Override
        public Class<PlayerLevelRankingValue> getValueClass() {
            return PlayerLevelRankingValue.class;
        }
    },
    ;
    private int id;
    private String name;
    //排序时间间隔毫秒
    private int rankPeriod;

    //是否需要入库
    private boolean needSvDB;
    private AbstractRankingHandler rankingHandler;

    public abstract CommRankData createCommRankData(RankSubType subType);

    public abstract <T extends IRankingObject> Class<T> getTypeClass();

    public abstract <T extends IRankingValue> Class<T> getValueClass();

    RankType(int id, String name, boolean needSvDB, int rankPeriod, AbstractRankingHandler rankingHandler) {
        this.id = id;
        this.name = name;
        this.needSvDB = needSvDB;
        this.rankPeriod = rankPeriod;
        this.rankingHandler = rankingHandler;
    }

    public boolean isNeedSvDB() {
        return this.needSvDB;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRankPeriod() {
        return rankPeriod;
    }

    /**
     * 低于最小值
     *
     * @param rankingValue
     * @return
     */
    public boolean isLowMin(IRankingValue rankingValue) {
        IRankingValue minRankValue = RankManager.getInstance().getRankResource(this).getMinRankValue();
        if (ObjectUtils.isEmpty(minRankValue)) {
            return false;
        }
        return rankingValue.compareTo(minRankValue) == WaveType.DOWM;
    }

    public AbstractRankingHandler getRankingHandler() {
        return rankingHandler;
    }

}
