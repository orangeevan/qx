package com.mmorpg.qx.module.rank.service;

import com.haipaite.common.utility.ThreadUtils;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.rank.entity.RankKey;
import com.mmorpg.qx.module.rank.enums.RankSubType;
import com.mmorpg.qx.module.rank.enums.RankType;
import com.mmorpg.qx.module.rank.manager.RankManager;
import com.mmorpg.qx.module.rank.model.IRankingObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wang ke
 * @description:
 * @since 13:57 2021/3/11
 */
@Component
public class RankService {

    private Logger logger = SysLoggerFactory.getLogger(RankService.class);

    private ScheduledExecutorService scheduledExecutorService;
    @Autowired
    private RankManager rankManager;

    public void init() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "RankServiceRankingObjecthread"));
    }

    /**
     * 获取前top名
     */
    public List<IRankingObject> rankList(RankType type, RankSubType subType, int top) {
        return subRankList(type, subType, 0, top);
    }

    /**
     * 获取一个排名区间的数据
     *
     * @param start
     * @param end
     * @return
     */
    public List<IRankingObject> subRankList(RankType type, RankSubType subType, int start, int end) {
        List<IRankingObject> empty = new ArrayList<>();
        List<IRankingObject> safeList = RankManager.getInstance().getCommRankData(type, subType).getCacheOrderRD();
        if (safeList == null || safeList.isEmpty()) {
            return empty;
        }
        if (start < 0 || start >= end || start >= safeList.size()) {
            logger.error("获取排行榜[" + type + "]从[" + start + "]到[" + end + "]的数据,size=" + safeList.size());
            return empty;
        }
        end = Math.min(end, safeList.size());
        return safeList.subList(start, end);
    }


    public void startRank() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!RankManager.getInstance().canRanking()) {
                return;
            }
            rankManager.flushRanks(false);

        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }


    private void shutdown() {
        ThreadUtils.shutdownGracefully(scheduledExecutorService, 1000, TimeUnit.MILLISECONDS);
    }

    public void processShutdown() {
        try {
            rankManager.flushRanks(true);
            shutdown();
        } finally {

        }
    }

    public void reqRankingList(Player player, RankKey key) {
        key.getType().getRankingHandler().sentRankingListResp(player, key.getType(), key.getSubType());
    }

    public void reqRankingSelf(Player player, RankKey key) {
        key.getType().getRankingHandler().sentRankingSelfResp(player, key.getType(), key.getSubType());
    }
}
