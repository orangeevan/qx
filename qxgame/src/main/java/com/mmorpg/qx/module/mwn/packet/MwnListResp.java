package com.mmorpg.qx.module.mwn.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.mwn.packet.vo.MoWuNiangVo;

import java.util.List;

/**
 * @author zhang peng
 * @description:
 * @since 10:40 2021/3/13
 */
@SocketPacket(packetId = PacketId.MWN_LIST_RESP)
public class MwnListResp {

    @Protobuf(description = "魔物娘列表")
    private List<MoWuNiangVo> moWuNiangVos;

    public List<MoWuNiangVo> getMoWuNiangVos() {
        return moWuNiangVos;
    }

    public void setMoWuNiangVos(List<MoWuNiangVo> moWuNiangVos) {
        this.moWuNiangVos = moWuNiangVos;
    }
}
