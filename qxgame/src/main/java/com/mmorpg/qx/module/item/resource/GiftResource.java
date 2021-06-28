package com.mmorpg.qx.module.item.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

import java.util.List;

/**
 * @author zhang peng
 * @description
 * @since 14:21 2021/4/26
 */
@Resource
public class GiftResource {

    // 唯一ID
    @Id
    private int id;
    // 礼包类型
    private int type;
    // 道具配置ID
    private List<Integer> itemIds;
    // 道具数量
    private List<Integer> nums;
    // 道具权重
    private List<Integer> weights;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Integer> itemIds) {
        this.itemIds = itemIds;
    }

    public List<Integer> getNums() {
        return nums;
    }

    public void setNums(List<Integer> nums) {
        this.nums = nums;
    }

    public List<Integer> getWeights() {
        return weights;
    }

    public void setWeights(List<Integer> weights) {
        this.weights = weights;
    }
}
