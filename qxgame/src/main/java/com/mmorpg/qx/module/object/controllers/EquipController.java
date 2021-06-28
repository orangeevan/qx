package com.mmorpg.qx.module.object.controllers;

import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.equipment.model.EquipItem;
import com.mmorpg.qx.module.object.controllers.packet.EquipSpawnResp;
import com.mmorpg.qx.module.roundFight.packet.RemoveFromMapResp;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wang ke
 * @description: 装备控制器
 * @since 14:55 2020-11-02
 */
public class EquipController extends AbstractVisibleObjectController<EquipItem> {
    @Override
    public void onSpawn(int mapId, int instanceId) {
        super.onSpawn(mapId, instanceId);
        WorldMapInstance mapInstance = WorldMapService.getInstance().getWorldMapInstance(mapId, instanceId);
        EquipItem owner = this.getOwner();
        EquipSpawnResp spawnResp = EquipSpawnResp.valueOf(Stream.of(owner).collect(Collectors.toList()));
        PacketSendUtility.broadcastInWorldMap(owner, spawnResp, false);
    }

    @Override
    public void onDespawn() {
        super.onDespawn();
        WorldMapInstance mapInstance = WorldMapService.getInstance().getWorldMapInstance(this.getMapId(), this.getMapInstanceId());
        //将生物从地图上移除
        EquipItem owner = this.getOwner();
        mapInstance.removeObject(owner);
        owner.setPosition(null);
        //做视野广播
        PacketSendUtility.broadcastInWorldMap(mapInstance, RemoveFromMapResp.valueOf(owner.getObjectId()), null);
    }
}
