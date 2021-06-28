package com.mmorpg.qx.module.object.controllers.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.equipment.model.EquipItem;

import java.util.List;

/**
 * @author wang ke
 * @description: 地图出现装备
 * @since 15:01 2020-11-02
 */
@SocketPacket(packetId = PacketId.EQUIP_INFO_RESP)
public class EquipSpawnResp {

    @Protobuf(description = "地图出现新装备")
    private List<EquipItem> equipItems;

    public static EquipSpawnResp valueOf(List<EquipItem> equipItems){
        EquipSpawnResp infoResp = new EquipSpawnResp();
        infoResp.equipItems = equipItems;
        return infoResp;
    }

    public List<EquipItem> getEquipItems() {
        return equipItems;
    }

    public void setEquipItems(List<EquipItem> equipItems) {
        this.equipItems = equipItems;
    }
}
