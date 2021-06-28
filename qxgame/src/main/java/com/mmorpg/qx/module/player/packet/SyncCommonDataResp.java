package com.mmorpg.qx.module.player.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.player.model.PlayerCommonType;

import java.util.Map;

/**
 * @author wang ke
 * @description: 同步玩家通用配置数据
 * @since 17:13 2020-10-19
 */
@SocketPacket(packetId = PacketId.SYNC_COMMON_DATA_RESP)
public class SyncCommonDataResp {
    @Protobuf(description = "同步玩家通用配置数据")
    private Map<PlayerCommonType, Integer> commonData;

    public static SyncCommonDataResp valueOf(Map<PlayerCommonType, Integer> commonData){
        SyncCommonDataResp resp = new SyncCommonDataResp();
        resp.setCommonData(commonData);
        return resp;
    }

    public Map<PlayerCommonType, Integer> getCommonData() {
        return commonData;
    }

    public void setCommonData(Map<PlayerCommonType, Integer> commonData) {
        this.commonData = commonData;
    }
}
