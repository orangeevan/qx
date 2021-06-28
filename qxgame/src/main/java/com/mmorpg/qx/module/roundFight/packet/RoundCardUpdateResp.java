package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.packet.vo.MoWuNiangVo;
import com.mmorpg.qx.module.object.Reason;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang ke
 * @description: 玩家卡包卡牌信息
 * @since 14:01 2020-08-14
 */
@SocketPacket(packetId = PacketId.PLAYER_CARD_UPDATE_RESP)
public class RoundCardUpdateResp {
    @Protobuf(description = "拥有者")
    private long owner;

    @Protobuf(description = "0表示失去 1代表获得")
    private int gainOrLoss;

    @Protobuf(description = "0：表示原卡包 1：正在使用中卡包")
    private int useCards;

    @Protobuf(description = "魔物娘卡牌信息")
    private List<MoWuNiangVo> updates;

    @Protobuf(description = "变化来源")
    private Reason reason;


    public static RoundCardUpdateResp valueOf(long trainerCreature, List<MoWuNiang> gains, boolean isGain, boolean isSource, Reason reason) {
        RoundCardUpdateResp resp = new RoundCardUpdateResp();
        resp.setOwner(trainerCreature);
        if (isSource) {
            resp.setUseCards(0);
        } else {
            resp.setUseCards(1);
        }
        if (isGain) {
            resp.setGainOrLoss(1);
        } else {
            resp.setGainOrLoss(0);
        }
        List<MoWuNiangVo> updateItems = new ArrayList<>();
        if (!CollectionUtils.isEmpty(gains)) {
            gains.forEach(gain -> updateItems.add(MoWuNiangVo.valueOf(gain)));
        }
        resp.setUpdates(updateItems);
        resp.reason = reason;
        return resp;
    }

    public List<MoWuNiangVo> getUpdates() {
        return updates;
    }

    public void setUpdates(List<MoWuNiangVo> updates) {
        this.updates = updates;
    }

    public int getGainOrLoss() {
        return gainOrLoss;
    }

    public void setGainOrLoss(int gainOrLoss) {
        this.gainOrLoss = gainOrLoss;
    }

    public int getUseCards() {
        return useCards;
    }

    public void setUseCards(int useCards) {
        this.useCards = useCards;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }
}
