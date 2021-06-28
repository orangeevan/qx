package com.mmorpg.qx.module.item.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

@Resource
public class ItemResource {

    // 物品唯一ID
    @Id
    private int id;
    // 物品名称
    private String name;
    // 物品品质
    private int quality;
    // 存储位置
    private int storePlace;
    // 存储上限
    private int overLimit;
    // 使用截止时间
    private String deadline;
    // 出售货币类型
    private int saleType;
    // 出售货币数量
    private int saleNum;
    // 使用效果类型(0和1不能使用)
    private int useType2;
    // 使用效果id
    private int useEffectId;
    // 使用效果num
    private int useEffectNum;
    // 使用效果-礼包ID
    private int giftId;
    // 使用消耗
    private int useCostItemId;
    // 扩展字段
    private String ext;
    //过期时间 deadline转化
    private long deadlineTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getStorePlace() {
        return storePlace;
    }

    public void setStorePlace(int storePlace) {
        this.storePlace = storePlace;
    }

    public int getOverLimit() {
        return overLimit;
    }

    public void setOverLimit(int overLimit) {
        this.overLimit = overLimit;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getSaleType() {
        return saleType;
    }

    public void setSaleType(int saleType) {
        this.saleType = saleType;
    }

    public int getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(int saleNum) {
        this.saleNum = saleNum;
    }

    public int getUseType2() {
        return useType2;
    }

    public void setUseType2(int useType2) {
        this.useType2 = useType2;
    }

    public int getUseEffectId() {
        return useEffectId;
    }

    public void setUseEffectId(int useEffectId) {
        this.useEffectId = useEffectId;
    }

    public int getUseEffectNum() {
        return useEffectNum;
    }

    public void setUseEffectNum(int useEffectNum) {
        this.useEffectNum = useEffectNum;
    }

    public int getUseCostItemId() {
        return useCostItemId;
    }

    public void setUseCostItemId(int useCostItemId) {
        this.useCostItemId = useCostItemId;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public int getGiftId() {
        return giftId;
    }

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    public long getDeadlineTime() {
        return deadlineTime;
    }

    public void setDeadlineTime(long deadlineTime) {
        this.deadlineTime = deadlineTime;
    }
}
