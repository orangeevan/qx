package com.mmorpg.qx.module.equipment.model;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import java.util.Objects;

/**
 * @author wang ke
 * @description: 装备同步前端信息
 * @since 14:03 2020-11-02
 */
public class EquipItemInfo {

    @Protobuf(description = "装备id")
    protected long objectId;
    /**
     * 装备配置id
     */
    @Protobuf(description = "配置表id")
    private int equipId;

    /**
     * 使用次数
     */
    @Protobuf(description = "使用次数")
    private int useTimes;

    /**
     * 表耐久度
     */
    @Protobuf(description = "耐久度")
    private int durability;

    public static EquipItemInfo valueOf(EquipItem equipItem) {
        EquipItemInfo info = new EquipItemInfo();
        if (Objects.isNull(equipItem)) {
            return info;
        }
        info.setEquipId(equipItem.getResource().getId());
        info.setUseTimes(equipItem.getUseTimes());
        info.setDurability(equipItem.getDurability());
        info.setObjectId(equipItem.getObjectId());
        return info;
    }

    public int getEquipId() {
        return equipId;
    }

    public void setEquipId(int equipId) {
        this.equipId = equipId;
    }

    public int getUseTimes() {
        return useTimes;
    }

    public void setUseTimes(int useTimes) {
        this.useTimes = useTimes;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void addUseTime() {
        useTimes++;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

}
