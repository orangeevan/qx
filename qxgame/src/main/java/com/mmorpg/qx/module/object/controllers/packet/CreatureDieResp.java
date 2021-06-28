package com.mmorpg.qx.module.object.controllers.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

@SocketPacket(packetId = PacketId.CREATURE_DIE)
public class CreatureDieResp {

    public static CreatureDieResp valueOf(long dieObjectId, long attackObjectId){
        CreatureDieResp creatureDieResp = new CreatureDieResp();
        creatureDieResp.setAttackObjectId(attackObjectId);
        creatureDieResp.setDieObjectId(dieObjectId);
        return creatureDieResp;
    }

    @Protobuf(description = "死亡者id")
    private long dieObjectId;

    @Protobuf(description = "攻击者id")
    private long attackObjectId;

    public long getDieObjectId() {
        return dieObjectId;
    }

    public void setDieObjectId(long dieObjectId) {
        this.dieObjectId = dieObjectId;
    }

    public long getAttackObjectId() {
        return attackObjectId;
    }

    public void setAttackObjectId(long attackObjectId) {
        this.attackObjectId = attackObjectId;
    }
}
