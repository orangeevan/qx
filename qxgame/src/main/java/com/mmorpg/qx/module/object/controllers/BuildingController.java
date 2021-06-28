package com.mmorpg.qx.module.object.controllers;

import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.building.AbstractBuilding;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;

/**
 * @author wang ke
 * @description: 建筑控制器
 * @since 16:03 2020-08-31
 */
public class BuildingController extends AbstractVisibleObjectController<AbstractBuilding> {

    @Override
    public void onSpawn(int mapId, int instanceId) {
        WorldMapInstance mapInstance = WorldMapService.getInstance().getWorldMapInstance(mapId, instanceId);
        AbstractBuilding owner = this.getOwner();
        PacketSendUtility.broadcastInWorldMap(owner, owner.getBuildingInfo(), false);
    }

    @Override
    public void see(AbstractVisibleObject object) {
        super.see(object);
        if (object instanceof PlayerTrainerCreature) {
            PacketSendUtility.sendPacket(object, getOwner().getBuildingInfo());
        }
    }
}
