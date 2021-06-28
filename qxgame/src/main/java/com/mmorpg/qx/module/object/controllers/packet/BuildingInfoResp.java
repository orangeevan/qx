package com.mmorpg.qx.module.object.controllers.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.object.gameobject.building.BuildingInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang ke
 * @description:建筑数据
 * @since 16:45 2020-08-31
 */
@SocketPacket(packetId = PacketId.BUILDING_INFO_RESP)
public class BuildingInfoResp {

    @Protobuf
    List<BuildingInfo> buildInfos;

    public static BuildingInfoResp valueOf(List<BuildingInfo> buildInfos) {
        BuildingInfoResp infoResp = new BuildingInfoResp();
        infoResp.setBuildInfos(buildInfos);
        return infoResp;
    }

    public static BuildingInfoResp valueOf(BuildingInfo buildingInfo) {
        BuildingInfoResp infoResp = new BuildingInfoResp();
        List<BuildingInfo> buildInfos = new ArrayList<>();
        buildInfos.add(buildingInfo);
        infoResp.setBuildInfos(buildInfos);
        return infoResp;
    }

    public List<BuildingInfo> getBuildInfos() {
        return buildInfos;
    }

    public void setBuildInfos(List<BuildingInfo> buildInfos) {
        this.buildInfos = buildInfos;
    }
}
