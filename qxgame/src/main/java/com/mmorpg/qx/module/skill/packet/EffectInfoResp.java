package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.skill.packet.vo.EffectVo;

import java.util.List;

/**
 * @author wang ke
 * @description: 效果信息
 * @since 15:36 2020-09-11
 */
@SocketPacket(packetId = PacketId.EFFECT_INFO_RESP)
public class EffectInfoResp {
    @Protobuf(description = "效果拥有者")
    private long ownerId;

    @Protobuf(description = "效果集合")
    private List<EffectVo> effectList;

    public static EffectInfoResp valueOf(long ownerId, List<EffectVo> effectList) {
        EffectInfoResp infoResp = new EffectInfoResp();
        infoResp.setOwnerId(ownerId);
        infoResp.setEffectList(effectList);
        return infoResp;
    }


    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public List<EffectVo> getEffectList() {
        return effectList;
    }

    public void setEffectList(List<EffectVo> effectList) {
        this.effectList = effectList;
    }
}
