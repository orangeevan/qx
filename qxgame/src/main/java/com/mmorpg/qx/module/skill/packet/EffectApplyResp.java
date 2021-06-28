package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.packet.vo.EffectVo;

import java.util.List;

@SocketPacket(packetId = PacketId.EFFECT_APPLY_RESP)
public class EffectApplyResp {
    @Protobuf(description = "释放者")
    private long casterId;

    @Protobuf(description = "持有者")
    private long ownerId;

    @Protobuf(description = "效果目标对象id,可能有多个")
    private List<GameUtil.LongVo> objects;

    @Protobuf(description = "效果数据")
    private EffectVo effectVo;

    public static EffectApplyResp valueOf(Effect effect) {
        EffectApplyResp resp = new EffectApplyResp();
        resp.setCasterId(effect.getEffector().getObjectId());
        resp.setOwnerId(effect.getEffected().getObjectId());
        resp.setObjects(GameUtil.toLongVo(effect.getEffectTarget().getTargetIds()));
        resp.setEffectVo(EffectVo.valueOf(effect));
        return resp;
    }

    public List<GameUtil.LongVo> getObjects() {
        return objects;
    }

    public void setObjects(List<GameUtil.LongVo> objects) {
        this.objects = objects;
    }

    public long getCasterId() {
        return casterId;
    }

    public void setCasterId(long casterId) {
        this.casterId = casterId;
    }

    public EffectVo getEffectVo() {
        return effectVo;
    }

    public void setEffectVo(EffectVo effectVo) {
        this.effectVo = effectVo;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
