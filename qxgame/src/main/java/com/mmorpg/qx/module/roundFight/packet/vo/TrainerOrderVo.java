package com.mmorpg.qx.module.roundFight.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wang ke
 * @description: 驯养师先手信息
 * @since 21:01 2021/3/17
 */
public class TrainerOrderVo {
    @Protobuf(description = "驯养师Id")
    private long trainerId;
    @Protobuf(description = "点子数")
    private int dicePoint;

    public static List<TrainerOrderVo> buildTrainerOrderInfo(Map<Long, Integer> orderInfo) {
        List<TrainerOrderVo> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderInfo)) {
            orderInfo.forEach((key, value) ->
                    vos.add(TrainerOrderVo.valueOf(key, value))
            );
        }
        return vos;
    }

    public static TrainerOrderVo valueOf(long trainerId, int dicePoint) {
        TrainerOrderVo trainerOrderVo = new TrainerOrderVo();
        trainerOrderVo.setTrainerId(trainerId);
        trainerOrderVo.setDicePoint(dicePoint);
        return trainerOrderVo;
    }

    public long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(long trainerId) {
        this.trainerId = trainerId;
    }

    public int getDicePoint() {
        return dicePoint;
    }

    public void setDicePoint(int dicePoint) {
        this.dicePoint = dicePoint;
    }
}
