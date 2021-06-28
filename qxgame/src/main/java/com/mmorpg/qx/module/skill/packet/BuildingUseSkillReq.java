package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.skill.model.target.Target;

/**
 * @author wang ke
 * @description: 使用建筑技能
 * @since 17:13 2020-08-31
 */
@SocketPacket(packetId = PacketId.USE_BUILDING_SKILL_REQ)
public class BuildingUseSkillReq {
    @Protobuf(description = "技能索引")
    private int skillIdIIndex;

    @Protobuf(description = "地图建筑对象Id")
    private long buildingId;

    @Protobuf(description = "选择职业索引")
    private int jobTypeIndex;

    @Protobuf(description = "释放目标")
    private Target target;

    public int getSkillIdIIndex() {
        return skillIdIIndex;
    }

    public void setSkillIdIIndex(int skillIdIIndex) {
        this.skillIdIIndex = skillIdIIndex;
    }

    public long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(long buildingId) {
        this.buildingId = buildingId;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public int getJobTypeIndex() {
        return jobTypeIndex;
    }

    public void setJobTypeIndex(int jobTypeIndex) {
        this.jobTypeIndex = jobTypeIndex;
    }
}
