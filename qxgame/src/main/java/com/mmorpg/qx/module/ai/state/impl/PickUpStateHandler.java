package com.mmorpg.qx.module.ai.state.impl;

import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.equipment.model.EquipItem;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;

/**
 * @author wang ke
 * @description: 拾取物品
 * @since 17:13 2020-08-18
 */
public class PickUpStateHandler implements IStateHandler {

    @Override
    public AIState getState() {
        return AIState.Pick_Up_Item;
    }

    @Override
    public void handleState(AbstractTrainerCreature owner, Object... params) {
        WorldPosition position = owner.getPosition();
        WorldMapInstance worldMapInstance = owner.getWorldMapInstance();
        AbstractVisibleObject equpByGridId = worldMapInstance.getItemByGridId(position.getGridId());
        //TODO 判断是否能拾取
        if (equpByGridId != null && equpByGridId instanceof EquipItem) {
            EquipItem equipItem = (EquipItem) equpByGridId;
            //工匠职业驯养师穿戴耐久度不同,在此处处理驯养师职业点会根据魔物娘改变
            if (owner.getAttrController().hasAttr(AttrType.Job_Artizan)) {
                equipItem.setDurability(equipItem.getResource().getSpecialDurability());
            }
            //EquipmentResource resource = EquipmentManager.getInstance().getEquipResource(equpByGridId.getObjectKey());
            WorldMapService.getInstance().despawn(equpByGridId);
            owner.getEquipmentStorage().addEquip(equipItem);
        }
    }
}
