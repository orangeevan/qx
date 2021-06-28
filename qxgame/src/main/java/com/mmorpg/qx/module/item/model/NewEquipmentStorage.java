package com.mmorpg.qx.module.item.model;

import com.mmorpg.qx.module.equipment.model.EquipItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author wang ke
 * @description: 战时装备容器
 * @since 11:17 2020-10-19
 */
public class NewEquipmentStorage {

    private int size;

    private List<EquipItem> equips;

    public NewEquipmentStorage(int size) {
        this.size = size;
        equips = new ArrayList<>(size);
    }

    public boolean addEquip(EquipItem newEquip) {
        if (isFull()) {
            return false;
        }
        return equips.add(newEquip);
    }

    public boolean removeEquip(EquipItem equip) {
        return equips.remove(equip);
    }

    public EquipItem getEquipById(long id) {
        Optional<EquipItem> packItem = equips.stream().filter(item -> item.getObjectId() == id).findFirst();
        if (packItem.isPresent()) {
            return packItem.get();
        }
        return null;
    }

    public boolean isFull() {
        return equips.size() >= size;
    }

    public List<EquipItem> getEquips() {
        return new ArrayList<>(equips);
    }
}
