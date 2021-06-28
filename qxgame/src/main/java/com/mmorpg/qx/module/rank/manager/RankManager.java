package com.mmorpg.qx.module.rank.manager;

import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.haipaite.common.utility.JsonUtils;
import com.haipaite.common.utility.ObjectUtils;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.rank.entity.RankEntity;
import com.mmorpg.qx.module.rank.entity.RankKey;
import com.mmorpg.qx.module.rank.enums.RankSubType;
import com.mmorpg.qx.module.rank.enums.RankType;
import com.mmorpg.qx.module.rank.enums.WaveType;
import com.mmorpg.qx.module.rank.model.CommRankData;
import com.mmorpg.qx.module.rank.model.IRankingObject;
import com.mmorpg.qx.module.rank.model.LinkedList;
import com.mmorpg.qx.module.rank.resource.RankResource;
import com.mmorpg.qx.module.rank.service.RankService;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 排行榜管理器
 * <p>
 * Created by zhangpeng on 2021/1/22
 */
@Component
public class RankManager {
    private Logger logger = SysLoggerFactory.getLogger(RankManager.class);
    @Static
    private Storage<RankType, RankResource> rankResourceStorage;
    /**
     * 排行榜锁
     */
    // private final Map<RankType, ReentrantReadWriteLock> rankTypeLocks = new HashMap<>(10);
    /**
     * 排行榜数据容器
     */
    private final ConcurrentMap<RankType, Map<RankSubType, CommRankData>> rankDatas = new ConcurrentHashMap<>();

    private static RankManager self;

    private AtomicBoolean startRank = new AtomicBoolean(false);

    @Inject
    private EntityCacheService<RankKey, RankEntity> rankEntityCaches;

    @PostConstruct
    private void init() {
        if (startRank.compareAndSet(false, true)) {
            initRankDatas();
            RankService rankService = BeanService.getBean(RankService.class);
            rankService.init();
            rankService.startRank();
        }
        self = this;
    }

    /**
     * 获取排行榜
     *
     * @param type 排行榜类型
     * @return
     */
    public CommRankData getCommRankData(RankType type, RankSubType subType) {
        return rankDatas.getOrDefault(type, Collections.emptyMap()).get(subType);
    }

    private void initRankDatas() {
        for (RankType type : RankType.values()) {
            Map subDatas = rankDatas.computeIfAbsent(type, key -> new ConcurrentHashMap<>());
            for (RankSubType subType : RankSubType.values()) {
                CommRankData commRankData = type.createCommRankData(subType);
                if (type.isNeedSvDB()) {
                    RankEntity rankEntity = getOrCreateRankEntity(RankKey.valueOf(type, subType));
                    commRankData.initData(rankEntity);
                }
                subDatas.putIfAbsent(subType, commRankData);
            }
        }
    }

    public RankEntity getOrCreateRankEntity(RankKey key) {
        return rankEntityCaches.loadOrCreate(key, id -> {
            CommRankData crd = key.getType().createCommRankData(key.getSubType());
            return toEntity(null, crd);
        });
    }

    public RankEntity toEntity(RankEntity rankEntity, CommRankData rankData) {
        RankEntity entity = rankEntity;
        if (Objects.isNull(entity)) {
            entity = new RankEntity();
            entity.setKey(rankData.getKey());
        }
        entity.setJsonData(JsonUtils.object2String(rankData.getCacheOrderRD()));
        return entity;
    }

    public void update(RankType type, RankSubType subType) {
        if (!type.isNeedSvDB()) {
            return;
        }
        RankKey key = RankKey.valueOf(type, subType);
        RankEntity rankEntity = getOrCreateRankEntity(RankKey.valueOf(type, subType));
        CommRankData commRankData = getCommRankData(type, subType);

        rankEntity = toEntity(rankEntity, commRankData);
        System.err.println("json   " + rankEntity.getJsonData());
        rankEntityCaches.writeBack(key, rankEntity);
    }


    public boolean canRanking() {
        return startRank.get();
    }

    public static RankManager getInstance() {
        return self;
    }

    public final void shutdown() {
        if (startRank.compareAndSet(true, false)) {
            BeanService.getBean(RankService.class).processShutdown();
        }
    }

    public RankResource getRankResource(RankType rankType) {
        return rankResourceStorage.get(rankType, true);
    }

    /**
     * 提交成员
     *
     * @param data
     */
    public void commit(RankType type, RankSubType subType, IRankingObject data) {
        if (!RankManager.getInstance().canRanking()) {
            return;
        }

        CommRankData rankData = RankManager.getInstance().getCommRankData(type, subType);
        rankData.putCommit(data.getId(), data);
        if (rankData.getRankPeriod() == 0) {
            flushRanks(type, subType, true);
        }
    }

    private void submit(CommRankData rankData, IRankingObject newObject) {
        RankType rankType = rankData.getKey().getType();
        RankSubType subType = rankData.getKey().getSubType();
        RankResource rankResource = RankManager.getInstance().getRankResource(rankType);
        if (rankType.isLowMin(newObject.getValue())) {
            return;
        }
        LinkedList<IRankingObject> list = rankData.getList();
        IRankingObject oldObject = rankData.getRankingObject(newObject.getId());
        WaveType waveType;
        //有旧数据，先移除，再把新值放在刚才位置。否则加到末尾
        if (!ObjectUtils.isEmpty(oldObject)) {
            waveType = newObject.getValue().compareTo(oldObject.getValue());
            //newObject.setRank(oldObject.getRank());
            LinkedList.Node<IRankingObject> oldNode = list.get(oldObject);
            list.replace(oldNode, newObject);
        } else {
            LinkedList.Node<IRankingObject> last = list.getLast();
            boolean isFull = list.size() >= rankResource.getMax();
            if (isFull) {
                boolean low2last = last != null && newObject.getValue().compareTo(last.getItem().getValue()) == WaveType.DOWM;
                if (low2last) {
                    return;
                }
                list.remove(last);
            }
            waveType = WaveType.UP;
            list.addLast(newObject);
        }
        sortRank(list, newObject, waveType);
        LinkedList.Node<IRankingObject> last = list.getLast();
        boolean fallout = rankResource.isFallOut() && rankType.isLowMin(last.getItem().getValue());
        if (fallout || list.size() > rankResource.getMax()) {
            list.remove(last);
        }
        rankData.updateOrderRankData();
        RankManager.getInstance().update(rankType, subType);
    }

    private void sortRank(LinkedList<IRankingObject> orderList, IRankingObject newObject, WaveType waveType) {
        if (orderList.size() <= 1 || waveType == WaveType.NONE) {
            return;
        }
        LinkedList.Node<IRankingObject> curNode = orderList.get(newObject);
        LinkedList.Node<IRankingObject> nextNode;
        if (waveType == WaveType.UP) {
            nextNode = orderList.getPrev(curNode);
            while (!orderList.isHead(nextNode) && curNode.getItem().getValue().compareTo(nextNode.getItem().getValue()) == waveType) {
                nextNode = orderList.getPrev(nextNode);
            }
            orderList.remove(curNode);
            orderList.addAfter(nextNode, newObject);
        } else {
            nextNode = orderList.getNext(curNode);
            while (!orderList.isTail(nextNode) && curNode.getItem().getValue().compareTo(nextNode.getItem().getValue()) == waveType) {
                nextNode = orderList.getNext(nextNode);
            }
            orderList.remove(curNode);
            orderList.addBefore(nextNode, newObject);
        }

    }


    public void rank(RankType type, RankSubType subType, boolean forceRank) {
        CommRankData rankData = RankManager.getInstance().getCommRankData(type, subType);
        synchronized (rankData) {
            if (rankData.getCommitSize() == 0) {
                return;
            }
            long now = System.currentTimeMillis();
            if (!forceRank && rankData.getLastRankTime() + rankData.getRankPeriod() > now) {
                return;
            }
            rankData.setLastRankTime(now);
            Object[] keys = rankData.getCommitKeys();

            for (Object key : keys) {
                IRankingObject rankingObject = rankData.removeCommits(key);
                submit(rankData, rankingObject);
            }
        }

        logger.info("update {} 排行榜", type);
    }

    /**
     * 强制排序所有数据
     */
    public void flushRanks(boolean forceRank) {
        for (RankType type : RankType.values()) {
            for (RankSubType subType : RankSubType.values()) {
                flushRanks(type, subType, forceRank);
            }
        }
    }

    public void flushRanks(RankType type, RankSubType subType, boolean forceRank) {
        rank(type, subType, forceRank);
    }
}
