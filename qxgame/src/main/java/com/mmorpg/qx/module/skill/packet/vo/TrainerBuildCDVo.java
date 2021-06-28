package com.mmorpg.qx.module.skill.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * @author wang ke
 * @description: 驯养师操作建筑CD数据
 * @since 19:37 2020-12-01
 */
public class TrainerBuildCDVo {
    @Protobuf(description = "建筑对象ID，唯一")
    private long buildId;
    @Protobuf(description = "建筑操作回合CD")
    private int roundCd;

    public static TrainerBuildCDVo valueOf(long building, int roundCd) {
        TrainerBuildCDVo buildCDVo = new TrainerBuildCDVo();
        buildCDVo.buildId = building;
        buildCDVo.roundCd = roundCd;
        return buildCDVo;
    }

    public long getBuildId() {
        return buildId;
    }

    public void setBuildId(long buildId) {
        this.buildId = buildId;
    }

    public int getRoundCd() {
        return roundCd;
    }

    public void setRoundCd(int roundCd) {
        this.roundCd = roundCd;
    }
}
