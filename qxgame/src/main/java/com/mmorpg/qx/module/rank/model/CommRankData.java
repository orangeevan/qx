package com.mmorpg.qx.module.rank.model;

import com.haipaite.common.utility.JsonUtils;
import com.haipaite.common.utility.ObjectUtils;
import com.haipaite.common.utility.StringUtils;
import com.mmorpg.qx.module.rank.entity.RankEntity;
import com.mmorpg.qx.module.rank.entity.RankKey;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by zhangpeng on 2021/1/12
 */
public class CommRankData<PK, T extends IRankingObject> {

    private final RankKey key;

    private final int rankPeriod;

    private final LinkedList<T> list = new LinkedList<>();

    private final ConcurrentMap<PK, T> commits = new ConcurrentHashMap<>();

    private long lastRankTime;

    /**
     * 用于查询的排行榜（每次刷新排序列表时，重新赋值）
     */
    private volatile List<T> cacheOrderRD = Collections.EMPTY_LIST;

    /**
     * 实例化
     *
     * @param key  名称
     * @param sync 刷新时间间隔,0表示实时刷新
     */
    public CommRankData(RankKey key, int sync) {
        this.key = key;
        this.rankPeriod = sync;
    }

    /**
     * 当前容量
     */
    public int size() {
        return list.size();
    }

    public LinkedList<T> getList() {
        return list;
    }

    public void putCommit(PK pk, T t) {
        commits.put(pk, t);
    }

    public T removeCommits(PK pk) {
        return commits.remove(pk);
    }

    public Object[] getCommitKeys() {
        return commits.keySet().toArray();
    }

    public int getCommitSize() {
        return commits.size();
    }

    public RankKey getKey() {
        return key;
    }

    public int getRankPeriod() {
        return rankPeriod;
    }


    public long getLastRankTime() {
        return lastRankTime;
    }

    public void setLastRankTime(long lastRankTime) {
        this.lastRankTime = lastRankTime;
    }

    public void initData(RankEntity rankEntity) {
        if (StringUtils.isBlank(rankEntity.getJsonData())) {
            return;
        }
        List<T> tList = JsonUtils.string2List(rankEntity.getJsonData(), this.key.getType().getTypeClass());
        if (!CollectionUtils.isEmpty(tList)) {
            tList.forEach(ele -> list.addLast(ele));
        }
        lastRankTime = System.currentTimeMillis();
    }

    public T getRankingObject(PK pk) {
        List<T> copy = cacheOrderRD;
        for (T t : copy) {
            if (!t.getId().equals(pk)) {
                continue;
            }
            return t;
        }
        return null;
    }

    public void updateOrderRankData() {
        List<T> copy = new ArrayList<>(list.size());
        LinkedList.Node<T> node = list.getFirst();
        while (!ObjectUtils.isEmpty(node) && !list.isTail(node)) {
            copy.add(node.getItem());
            node = list.getNext(node);
        }
        cacheOrderRD = copy;
    }

    public List<T> getCacheOrderRD() {
        return cacheOrderRD;
    }
}
