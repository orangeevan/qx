package com.mmorpg.qx.module.troop.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.troop.manager.TroopWeightManager;

/**
 * @author zhang peng
 * @description 卡牌对象
 * @since 11:46 2021/4/9
 */
public class Card {

    @Protobuf(description = "魔物娘ID")
    private long mwnId;

    @Protobuf(description = "回合阶段 0:无 1:1-5回合 2:1-10回合")
    private int stage;

    @Protobuf(description = "权重调整 1:增加 2:减少")
    private int adjust;

    @Protobuf(description = "概率 1:小 2:中 3:大")
    private int prob;

    public static Card valueOf(long mwnId, int stage, int adjust, int prob) {
        Card card = new Card();
        card.mwnId = mwnId;
        card.stage = stage;
        card.adjust = adjust;
        card.prob = prob;
        return card;
    }

    /**
     * 获取权重 基础权重1000
     *
     * @param roundNum 回合数
     * @return
     */
    @JSONField(serialize = false)
    public int getWeight(int roundNum) {
        int weight = 1000;
        if ((stage == 1 && roundNum <= 5) || (stage == 2 && roundNum <= 10)) {
            int change = TroopWeightManager.getInstance().getResource(prob).getWeight();
            if (adjust == 1) {
                weight += change;
            } else {
                weight -= change;
            }
        }
        return weight;
    }

    /**
     * 拷贝对象
     */
    @JSONField(serialize = false)
    public Card copy() {
        Card card = new Card();
        card.mwnId = this.mwnId;
        card.stage = this.stage;
        card.adjust = adjust;
        card.prob = prob;
        return card;
    }


    public long getMwnId() {
        return mwnId;
    }

    public void setMwnId(long mwnId) {
        this.mwnId = mwnId;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getAdjust() {
        return adjust;
    }

    public void setAdjust(int adjust) {
        this.adjust = adjust;
    }

    public int getProb() {
        return prob;
    }

    public void setProb(int prob) {
        this.prob = prob;
    }
}
