package com.mmorpg.qx.module.troop.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 * @author zhang peng
 * @description
 * @since 11:07 2021/4/24
 */
@Resource
public class TroopWeightResource {

    // 唯一ID
    @Id
    private int id;
    // 概率
    private int prob;
    // 概率对应权重
    private int weight;
    // 魔物娘羁绊等级限制
    private int limitLv;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProb() {
        return prob;
    }

    public void setProb(int prob) {
        this.prob = prob;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getLimitLv() {
        return limitLv;
    }

    public void setLimitLv(int limitLv) {
        this.limitLv = limitLv;
    }
}
