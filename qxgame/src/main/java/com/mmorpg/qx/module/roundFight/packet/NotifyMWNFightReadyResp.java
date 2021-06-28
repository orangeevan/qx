package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.object.controllers.packet.MWNInfoResp;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;

/**
 * @author wang ke
 * @description: 通知玩家魔物娘战斗
 * @since 16:32 2020-08-28
 */
@SocketPacket(packetId = PacketId.NOTIFY_MWN_FIGHT_READY_RESP)
public class NotifyMWNFightReadyResp {

    @Protobuf(description = "攻击方魔物娘")
    private MWNInfoResp attacker;

    @Protobuf(description = "防御方魔物娘")
    private MWNInfoResp defender;

    public static NotifyMWNFightReadyResp valueOf(MWNCreature attacker, MWNCreature defender) {
        NotifyMWNFightReadyResp resp = new NotifyMWNFightReadyResp();
        resp.attacker = attacker.getInfo();
        resp.defender = defender.getInfo();
        return resp;
    }

    public MWNInfoResp getAttacker() {
        return attacker;
    }

    public void setAttacker(MWNInfoResp attacker) {
        this.attacker = attacker;
    }

    public MWNInfoResp getDefender() {
        return defender;
    }

    public void setDefender(MWNInfoResp defender) {
        this.defender = defender;
    }
}
