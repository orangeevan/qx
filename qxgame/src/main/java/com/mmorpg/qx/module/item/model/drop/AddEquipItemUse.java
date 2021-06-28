package com.mmorpg.qx.module.item.model.drop;

import com.mmorpg.qx.module.equipment.manager.EquipmentManager;
import com.mmorpg.qx.module.equipment.model.EquipItem;
import com.mmorpg.qx.module.equipment.resource.EquipmentResource;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.DropItemCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.object.gameobject.enums.DropType;

/**
 * @author wang ke
 * @description: 添加装备
 * @since 17:16 2020-10-12
 */
public class AddEquipItemUse extends AbstractDropItemUse {

    @Override
    boolean doUseDropItem(AbstractTrainerCreature trainer, DropItemCreature dropItem) {
        if (dropItem.getDropType() != getDropType()) {
            throw new RuntimeException(String.format("掉落物处理类型错误, 掉落物 [%s] ,掉落类型 [%s]", dropItem.getName() + dropItem.getDropType()));
        }

        if (trainer.getEquipmentStorage().isFull()) {
            return false;
        }
        int resourceId = dropItem.getObjectKey();
        EquipmentResource resource = EquipmentManager.getInstance().getEquipResource(resourceId);
        EquipItem equipItem = EquipmentManager.getInstance().genEquipItem(resource);
        //工匠职业驯养师穿戴耐久度不同,在此处处理驯养师职业点会根据魔物娘改变
        if (trainer.getAttrController().hasAttr(AttrType.Job_Artizan)) {
            equipItem.setDurability(equipItem.getResource().getSpecialDurability());
        }
        trainer.getEquipmentStorage().addEquip(equipItem);
        return true;
    }

    @Override
    DropType getDropType() {
        return DropType.Equip;
    }
}
