package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

/**
 * @author wang ke
 * @description: HP MP改变同步前端
 * @since 15:33 2020-09-09
 */
@SocketPacket(packetId = PacketId.LIFE_CHANGE_RESP)
public class LifeChangeResp {
    public static LifeChangeResp valueOf(Reason reason, AbstractCreature creature) {
        LifeChangeResp resp = new LifeChangeResp();
        resp.setReason(reason);
        resp.setCurHp(creature.getCurrentHp());
        resp.setCurMp(creature.getLifeStats().getCurrentMp());
        resp.setGold(creature.getLifeStats().getCurrentGold());
        resp.setId(creature.getObjectId());
        resp.setObjectType(creature.getObjectType().getValue());
        return resp;
    }

    @Protobuf(description = " 2：魔物娘 4: 机器人驯养师  7: 玩家驯养师")
    private int objectType;

    @Protobuf(description = "地图对象唯一id")
    private long id;

    @Protobuf(description = "变化原因")
    private Reason reason;

    @Protobuf(description = "变化后血量")
    private int curHp;

    @Protobuf(description = "变化后魔法")
    private int curMp;

    @Protobuf(description = "变化后金币")
    private int gold;

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public int getCurHp() {
        return curHp;
    }

    public void setCurHp(int curHp) {
        this.curHp = curHp;
    }

    public int getCurMp() {
        return curMp;
    }

    public void setCurMp(int curMp) {
        this.curMp = curMp;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
