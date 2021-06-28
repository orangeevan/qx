package com.mmorpg.qx.module.item.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmorpg.qx.module.item.enums.EquipmentType;
import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.player.model.Player;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 装备容器
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public class EquipmentStorage {


    private PackItem[] equipments;

    @JSONField(serialize = false)
    private transient BitSet mark;

    private transient Player owner;

    protected void update() {
        if (getOwner() != null) {
            ItemManager.getInstance().update(getOwner());
        }
    }

    public static EquipmentStorage valueOf() {
        return new EquipmentStorage();
    }

    public EquipmentStorage() {
        this(ItemManager.getInstance().getCardBagsMaxCardNum());
    }

    public EquipmentStorage(int size) {
        equipments = new PackItem[size];
        mark = new BitSet(size);
    }

    @JSONField(serialize = false)
    public PackItem getByGuid(long guid) {
        for (PackItem equipment : equipments) {
            if (equipment != null && equipment.getObjectId().longValue() == guid) {
                return equipment;
            }
        }
        return null;
    }

    public PackItem[] getEquipments() {
        return equipments;
    }

    public void setEquipments(PackItem[] equipments) {
        this.equipments = equipments;
    }

    @JSONField(serialize = false)
    public PackItem getEquip(int ordinal) {
        if (ordinal < 0 || ordinal >= equipments.length) {
            return null;
        }
        return equipments[ordinal];
    }

    @JSONField(serialize = false)
    public PackItem getEquip(EquipmentType type) {
        return equipments[type.getIndex()];
    }

    @JSONField(serialize = false)
    public boolean hasEquip(EquipmentType type) {
        return equipments[type.getIndex()] != null;
    }

//    @JSONField(serialize = false)
//    public PackItem equip(PackItem equipment) {
//        int index = equipment.getEquipmentType().getIndex();
//        PackItem result = equipments[index];
//        equipments[index] = equipment;
//        mark.set(index);
//        update();
//        return result;
//    }

    @JSONField(serialize = false)
    public PackItem unEquip(EquipmentType type) {
        int index = type.getIndex();
        PackItem result = equipments[index];
        equipments[index] = null;
        if (result != null) {
            mark.set(index);
            update();
        }
        return result;
    }

    @JSONField(serialize = false)
    public Map<Integer, PackItem> collectUpdate() {
        HashMap<Integer, PackItem> result = new LinkedHashMap<Integer, PackItem>();
        for (int i = 0, j = mark.length(); i < j; i++) {
            if (mark.get(i)) {
                if (equipments[i] == null) {
                    // TODO 测试PB3是否支持null value
                    result.put(i, null);
                    mark.set(i, false);
                } else {
                    result.put(i, equipments[i]);
                }
            }
        }
        return result;
    }

    @JSONField(serialize = false)
    public void markByEquipmentType(EquipmentType type) {
        int index = type.getIndex();
        mark.set(index);
        update();
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public PackItem getItemByIndex(int index) {
        if (index >= 0 && index < equipments.length) {
            return equipments[index];
        }
        return null;
    }
}
