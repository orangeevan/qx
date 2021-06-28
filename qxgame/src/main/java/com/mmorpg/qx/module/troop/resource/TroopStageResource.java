package com.mmorpg.qx.module.troop.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 * @author zhang peng
 * @description
 * @since 11:39 2021/4/24
 */
@Resource
public class TroopStageResource {

    // 唯一ID
    @Id
    private int id;
    // 回合阶段
    private int roundStage;
    // 羁绊等级限制
    private int limitLv;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoundStage() {
        return roundStage;
    }

    public void setRoundStage(int roundStage) {
        this.roundStage = roundStage;
    }

    public int getLimitLv() {
        return limitLv;
    }

    public void setLimitLv(int limitLv) {
        this.limitLv = limitLv;
    }
}
