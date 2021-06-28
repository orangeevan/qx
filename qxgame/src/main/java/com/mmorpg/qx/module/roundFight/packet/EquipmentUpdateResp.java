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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wang ke
 * @description: 战斗中装备栏更新
 * @since 14:52 2020-08-28
 */
@SocketPacket(packetId = PacketId.EQUIPMENT_UPDATE_RESP)
public class EquipmentUpdateResp {
    @Protobuf(description = "拥有者")
    private long owner;

    @Protobuf(description = "0:失去 1：获得")
    private int gain;

    @Protobuf(description = "获得或失去装备")
    private List<EquipItemInfo> updateItems;

    public static EquipmentUpdateResp valueOf(AbstractTrainerCreature trainerCreature, EquipItem item, boolean isGain) {
        EquipmentUpdateResp resp = valueOf(trainerCreature, Stream.of(item).collect(Collectors.toList()), isGain);
        return resp;
    }

    public static EquipmentUpdateResp valueOf(AbstractTrainerCreature trainerCreature, List<EquipItem> updateItems, boolean isGain) {
        EquipmentUpdateResp resp = new EquipmentUpdateResp();
        List<EquipItemInfo> items = new ArrayList<>();
        if (!CollectionUtils.isEmpty(updateItems)) {
            updateItems.stream().forEach(equipItem -> items.add(EquipItemInfo.valueOf(equipItem)));
        }
        resp.updateItems = items;
        resp.gain = isGain ? 1 : 0;
        resp.owner = trainerCreature.getObjectId();
        return resp;
    }

    public List<EquipItemInfo> getUpdateItems() {
        return updateItems;
    }

    public void setUpdateItems(List<EquipItemInfo> updateItems) {
        this.updateItems = updateItems;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public int getGain() {
        return gain;
    }

    public void setGain(int gain) {
        this.gain = gain;
    }
}
