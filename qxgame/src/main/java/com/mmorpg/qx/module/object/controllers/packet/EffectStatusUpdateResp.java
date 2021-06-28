package com.mmorpg.qx.module.object.controllers.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;

@SocketPacket(packetId = PacketId.ABNORMAL_EFFECT_RESP)
public class EffectStatusUpdateResp {
    @Protobuf(description = "状态")
    private EffectStatus effectStatus;

    @Protobuf(description = "失去：0  1：获得")
    private int loss;

    public EffectStatus getEffectStatus() {
        return effectStatus;
    }

    public void setEffectStatus(EffectStatus effectStatus) {
        this.effectStatus = effectStatus;
    }

    public int getLoss() {
        return loss;
    }

    public void setLoss(int loss) {
        this.loss = loss;
    }

    public static EffectStatusUpdateResp value(EffectStatus effectStatus, boolean gain) {
        EffectStatusUpdateResp resp = new EffectStatusUpdateResp();
        resp.effectStatus = effectStatus;
        resp.loss = gain ? 1 : 0;
        return resp;
    }
}
