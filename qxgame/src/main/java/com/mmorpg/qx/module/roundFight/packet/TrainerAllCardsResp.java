package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.packet.vo.MoWuNiangVo;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 驯养师卡牌信息
 * @since 15:12 2020-12-08
 */
@SocketPacket(packetId = PacketId.TRAINER_ALL_CARDS_RESP)
public class TrainerAllCardsResp {
    @Protobuf(description = "卡组剩余卡牌信息")
    private List<MoWuNiangVo> sourceCardsInfo;

    @Protobuf(description = "卡包剩余卡牌信息")
    private List<MoWuNiangVo> useCardsInfo;

    public static TrainerAllCardsResp valueOf(AbstractTrainerCreature creature){
        TrainerAllCardsResp allCardsResp = new TrainerAllCardsResp();
        List<MoWuNiang> sourceCards = creature.getSourceCardStorage().getMwns();
        List<MoWuNiangVo> sc = new ArrayList<>();
        if(!CollectionUtils.isEmpty(sourceCards)){
            sc = sourceCards.stream().map(MoWuNiangVo::valueOf).collect(Collectors.toList());
        }
        allCardsResp.setSourceCardsInfo(sc);
        List<MoWuNiangVo> uc = new ArrayList<>();
        List<MoWuNiang> useCards = creature.getUseCardStorage().getMwns();
        if (!CollectionUtils.isEmpty(useCards)) {
            uc = useCards.stream().map(MoWuNiangVo::valueOf).collect(Collectors.toList());
        }
        allCardsResp.setUseCardsInfo(uc);
        return allCardsResp;
    }

    public List<MoWuNiangVo> getSourceCardsInfo() {
        return sourceCardsInfo;
    }

    public void setSourceCardsInfo(List<MoWuNiangVo> sourceCardsInfo) {
        this.sourceCardsInfo = sourceCardsInfo;
    }

    public List<MoWuNiangVo> getUseCardsInfo() {
        return useCardsInfo;
    }

    public void setUseCardsInfo(List<MoWuNiangVo> useCardsInfo) {
        this.useCardsInfo = useCardsInfo;
    }
}
