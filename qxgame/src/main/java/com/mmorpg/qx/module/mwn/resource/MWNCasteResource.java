package com.mmorpg.qx.module.mwn.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

import java.util.List;

/**
 * @author zhang peng
 * @description:
 * @since 17:22 2021/3/13
 */
@Resource
public class MWNCasteResource {

    @Id
    private int id;
    // 魔物娘配置id
    private int mwnKey;
    // 魔物娘阶数
    private int casteLv;
    // 消耗道具ID
    private List<Integer> costIds;
    // 消耗道具数量
    private List<Integer> nums;
    // 升阶等级限制
    private int levelLimit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMwnKey() {
        return mwnKey;
    }

    public void setMwnKey(int mwnKey) {
        this.mwnKey = mwnKey;
    }

    public int getCasteLv() {
        return casteLv;
    }

    public void setCasteLv(int casteLv) {
        this.casteLv = casteLv;
    }

    public List<Integer> getCostIds() {
        return costIds;
    }

    public void setCostIds(List<Integer> costIds) {
        this.costIds = costIds;
    }

    public List<Integer> getNums() {
        return nums;
    }

    public void setNums(List<Integer> nums) {
        this.nums = nums;
    }

    public int getLevelLimit() {
        return levelLimit;
    }

    public void setLevelLimit(int levelLimit) {
        this.levelLimit = levelLimit;
    }
}
