package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.packet.vo.EffectVo;

/**
 * @author wang ke
 * @description: 效果添加
 * @since 15:58 2020-11-04
 */
@SocketPacket(packetId = PacketId.EFFECT_ADD_RESP)
public class EffectAddResp {
    @Protobuf(description = "释放者")
    private long casterId;
    @Protobuf(description = "效果拥有对象id")
    private long objectId;
    @Protobuf(description = "效果数据")
    private EffectVo effectVo;

    public static EffectAddResp valueOf(Effect effect) {
        EffectAddResp resp = new EffectAddResp();
        resp.setCasterId(effect.getEffector().getObjectId());
        resp.setObjectId(effect.getEffected().getObjectId());
        resp.setEffectVo(EffectVo.valueOf(effect));
        return resp;
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

    public EffectVo getEffectVo() {
        return effectVo;
    }

    public void setEffectVo(EffectVo effectVo) {
        this.effectVo = effectVo;
    }
}
