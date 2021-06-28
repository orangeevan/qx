package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;

import static com.mmorpg.qx.common.PacketId.TRAINER_USECARD_NNUM_RESP;

/**
 * @author wang ke
 * @description:
 * @since 17:10 2020-11-16
 */
@SocketPacket(packetId = TRAINER_USECARD_NNUM_RESP)
public class TrainerUseCardNumResp {
    @Protobuf(description = "卡包拥有者")
    private long owenrId;

    @Protobuf(description = "卡包剩余卡牌数量")
    private int sourceNum;

    @Protobuf(description = "当前回合中使用卡牌数量")
    private int useNum;

    public static TrainerUseCardNumResp valueOf(AbstractTrainerCreature creature){
        TrainerUseCardNumResp resp = new TrainerUseCardNumResp();
        resp.owenrId = creature.getObjectId();
        resp.sourceNum = creature.getSourceCardStorage().getCurrentSize();
        resp.useNum = creature.getUseCardStorage().getCurrentSize();
        return resp;
    }

    public long getOwenrId() {
        return owenrId;
    }

    public void setOwenrId(long owenrId) {
        this.owenrId = owenrId;
    }

    public int getSourceNum() {
        return sourceNum;
    }

    public void setSourceNum(int sourceNum) {
        this.sourceNum = sourceNum;
    }

    public int getUseNum() {
        return useNum;
    }

    public void setUseNum(int useNum) {
        this.useNum = useNum;
    }
}

