package com.mmorpg.qx.module.skill.model.target;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 目标选择参数,不同的技能选择不同的参数来用
 *
 * @author wang ke
 * @since v1.0 2019年3月2日
 */
public class Target {
    @Protobuf(description = "目标对象Id")
    private List<Long> targetIds;
    @Protobuf(description = "目标格子号")
    private int gridId;

    public List<Long> getTargetIds() {
        return targetIds;
    }

    public void setTargetIds(List<Long> targetIds) {
        this.targetIds = targetIds;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }

    public static Target valueOf(int gridId, long... targetIds){
        Target target = new Target();
        target.setGridId(gridId);
        List<Long> ts = new ArrayList<>();
        Arrays.stream(targetIds).forEach(ts::add);
        target.setTargetIds(ts);
        return target;
    }

    public static Target valueOf(int gridId, List<Long> targetIds){
        Target target = new Target();
        target.setGridId(gridId);
        target.setTargetIds(targetIds);
        return target;
    }
}
