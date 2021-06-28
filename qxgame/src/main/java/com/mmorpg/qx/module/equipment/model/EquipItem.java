package com.mmorpg.qx.module.equipment.model;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.equipment.manager.EquipmentManager;
import com.mmorpg.qx.module.equipment.resource.EquipmentResource;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.AbstractVisibleObjectController;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;

/**
 * @author wang ke
 * @description: 装备道具，战斗时使用
 * @since 14:03 2020-11-02
 */
public class EquipItem extends AbstractVisibleObject {
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

    public EquipItem(long objId, EquipmentResource resource, AbstractVisibleObjectController<? extends AbstractVisibleObject> controller, WorldPosition position) {
        super(objId, controller, position);
        setEquipId(resource.getId());
        setUseTimes(0);
        //TODO 工匠职业持久度不一样
        setDurability(resource.getDurability());
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.Equip;
    }


    @Override
    public String getName() {
        return EquipmentManager.getInstance().getEquipResource(equipId).getName();
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

    public EquipmentResource getResource() {
        return EquipmentManager.getInstance().getEquipResource(equipId);
    }
}
