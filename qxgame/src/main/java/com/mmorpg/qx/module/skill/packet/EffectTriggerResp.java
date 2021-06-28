package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.skill.model.effect.Effect;

/**
 * @author wang ke
 * @description: 效果触发
 * @since 16:37 2020-10-27
 */
@SocketPacket(packetId = PacketId.EFFECT_TRIGGER_RESP)
public class EffectTriggerResp {
    @Protobuf(description = "效果配置id")
    private int effectId;

    @Protobuf(description = "释放者")
    private long casterId;

    @Protobuf(description = "效果拥有者对象id")
    private long objectId;

    public static EffectTriggerResp valueOf(Effect effect){
        EffectTriggerResp resp = new EffectTriggerResp();
       resp.setEffectId(effect.getEffectResourceId());
       resp.setCasterId(effect.getEffector().getObjectId());
       resp.setObjectId(effect.getEffected().getObjectId());
       return resp;
    }

    public int getEffectId() {
        return effectId;
    }

    public void setEffectId(int effectId) {
        this.effectId = effectId;
    }

    public long getCasterId() {
        return casterId;
    }

    public void setCasterId(long casterId) {
        this.casterId = casterId;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }
}
