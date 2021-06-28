package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.equipment.model.EquipItemInfo;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;

import java.util.List;

/**
 * @author wang ke
 * @description: 魔物娘间战斗战报
 * @since 15:55 2020-08-27
 */
@SocketPacket(packetId = PacketId.MWN_ROUND_FIGHT_DAMAGE_RESP)
public class MwnRoundFightReportResp {
    @Protobuf(description = "胜利方id")
    private long winner;

    @Protobuf(description = "进攻方")
    private long attackerId;

    @Protobuf(description = "进攻方穿戴装备")
    private EquipItemInfo attacker;

    @Protobuf(description = "防守方")
    private long defenderId;

    @Protobuf(description = "防守方穿戴装备")
    private EquipItemInfo defender;

    @Protobuf(description = "魔物娘间回合战斗战报")
    private List<AbstractSkillResult> reports;

    @Protobuf(description = "进攻方援护魔物娘皮肤ID")
    private int attSupporter;

    @Protobuf(description = "防守方援护魔物娘皮肤ID")
    private int defSupporter;

    public static MwnRoundFightReportResp valueOf(List<AbstractSkillResult> reports, long winner, MWNCreature attacker,
                                                  MWNCreature defender, int attSupporter, int defSupporter){
        MwnRoundFightReportResp resp = new MwnRoundFightReportResp();
        resp.reports = reports;
        resp.winner = winner;
        resp.attacker = EquipItemInfo.valueOf(attacker.getWearEquip());
        resp.defender =  EquipItemInfo.valueOf(defender.getWearEquip());
        resp.attackerId = attacker.getObjectId();
        resp.defenderId = defender.getObjectId();
        resp.attSupporter = attSupporter;
        resp.defSupporter = defSupporter;
        return resp;
    }

    public List<AbstractSkillResult> getReports() {
        return reports;
    }

    public void setReports(List<AbstractSkillResult> reports) {
        this.reports = reports;
    }

    public long getWinner() {
        return winner;
    }

    public void setWinner(long winner) {
        this.winner = winner;
    }

    public EquipItemInfo getAttacker() {
        return attacker;
    }

    public void setAttacker(EquipItemInfo attacker) {
        this.attacker = attacker;
    }

    public EquipItemInfo getDefender() {
        return defender;
    }

    public void setDefender(EquipItemInfo defender) {
        this.defender = defender;
    }

    public long getAttackerId() {
        return attackerId;
    }

    public void setAttackerId(long attackerId) {
        this.attackerId = attackerId;
    }

    public long getDefenderId() {
        return defenderId;
    }

    public void setDefenderId(long defenderId) {
        this.defenderId = defenderId;
    }

    public int getAttSupporter() {
        return attSupporter;
    }

    public void setAttSupporter(int attSupporter) {
        this.attSupporter = attSupporter;
    }

    public int getDefSupporter() {
        return defSupporter;
    }

    public void setDefSupporter(int defSupporter) {
        this.defSupporter = defSupporter;
    }
}
