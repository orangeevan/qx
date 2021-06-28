package com.mmorpg.qx.module.skill.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用技能广播
 *
 * @author wang ke
 * @since v1.0 2018年3月19日
 */
@SocketPacket(packetId = PacketId.USE_SKILL_RESP)
public class UseSkillResp {
    @Protobuf(description = "释放者")
    private long objectId;
    @Protobuf(description = "技能id")
    private int skillId;
    @Protobuf(description = "目标对象Id，方便前端")
    private List<GameUtil.LongVo> targetIds;

    @Protobuf(description = "技能伤害")
    private List<AbstractSkillResult> results;

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public List<GameUtil.LongVo> getTargetIds() {
        return targetIds;
    }

    public void setTargetIds(List<GameUtil.LongVo> targetIds) {
        this.targetIds = targetIds;
    }

    public List<AbstractSkillResult> getResults() {
        return results;
    }

    public void setResults(List<AbstractSkillResult> results) {
        this.results = results;
    }

    public UseSkillResp build(long objectId) {
        this.objectId = objectId;
        return this;
    }

    public UseSkillResp build(int skillId) {
        this.skillId = skillId;
        return this;
    }

    public UseSkillResp build(List<Long> targetIds) {
        if (CollectionUtils.isEmpty(targetIds)) {
            this.targetIds = new ArrayList<>();
        } else {
            this.targetIds = GameUtil.toLongVo(targetIds);
        }
        return this;
    }

    public UseSkillResp buildSkillResults(List<AbstractSkillResult> results) {
        this.results = results;
        return this;
    }
}
