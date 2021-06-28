package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.equipment.model.EquipItem;
import com.mmorpg.qx.module.equipment.model.EquipItemInfo;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang ke
 * @description: 返回装备栏信息
 * @since 11:55 2020-10-19
 */
@SocketPacket(packetId = PacketId.EQUIPMENT_INFO_RESP)
public class EquipmentInfoResp {
    @Protobuf(description = "拥有者")
    private long owner;

    @Protobuf(description = "装备栏信息")
    public List<EquipItemInfo> equipmentInfo;

    public static EquipmentInfoResp valueOf(List<EquipItem> equipmentInfo, AbstractTrainerCreature creature) {
        EquipmentInfoResp resp = new EquipmentInfoResp();
        resp.owner = creature.getObjectId();
        List<EquipItemInfo> items = new ArrayList<>();
        if (!CollectionUtils.isEmpty(equipmentInfo)) {
            equipmentInfo.stream().forEach(equipItem -> items.add(EquipItemInfo.valueOf(equipItem)));
        }
        resp.equipmentInfo = items;
        return resp;
    }

    public List<EquipItemInfo> getEquipmentInfo() {
        return equipmentInfo;
    }

    public void setEquipmentInfo(List<EquipItemInfo> equipmentInfo) {
        this.equipmentInfo = equipmentInfo;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }
}
