package com.mmorpg.qx.module.object.controllers.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.object.gameobject.packet.vo.AttrVo;
import com.mmorpg.qx.module.skill.packet.vo.EffectVo;
import com.mmorpg.qx.module.skill.packet.vo.SkillVo;

import java.util.List;

/**
 * 场景内魔物娘信息
 *
 * @author wang ke
 * @since v1.0 2018年3月27日
 */
@SocketPacket(packetId = PacketId.MWN_INFO_RESP)
public class MWNInfoResp {
    @Protobuf(description = "唯一对象id", order = 1)
    private long objectId;
    @Protobuf(description = "配置表id", order = 2)
    private int modelId;

    @Protobuf(description = "地图格子号", order = 3)
    private int gridId;
    @Protobuf(description = "初始方向", order = 4)
    private int dir;

    @Protobuf(order = 5)
    private int curHp;

    @Protobuf(order = 6)
    private int curMp;

    @Protobuf(description = "魔物娘皮肤id", order = 7)
    private int skinId;

    @Protobuf(description = "归属者", order = 8)
    private long owner;

    @Protobuf(description = "属性", order = 9)
    private List<AttrVo> attrs;

    @Protobuf(description = "技能", order = 10)
    private List<SkillVo> skill;

    @Protobuf(description = "buff效果", order = 11)
    private List<EffectVo> effects;

    @Protobuf(description = "等级", order = 12)
    private int level;

    @Protobuf(description = "(突破)阶数", order = 13)
    private int casteLv;

    @Protobuf(description = "(潜能)星数", order = 14)
    private int starLv;

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public List<AttrVo> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<AttrVo> attrs) {
        this.attrs = attrs;
    }

    public List<EffectVo> getEffects() {
        return effects;
    }

    public void setEffects(List<EffectVo> effects) {
        this.effects = effects;
    }

    public int getCurHp() {
        return curHp;
    }

    public void setCurHp(int curHp) {
        this.curHp = curHp;
    }

    public int getCurMp() {
        return curMp;
    }

    public void setCurMp(int curMp) {
        this.curMp = curMp;
    }

    public int getSkinId() {
        return skinId;
    }

    public void setSkinId(int skinId) {
        this.skinId = skinId;
    }

    public void setSkill(List<SkillVo> skill) {
        this.skill = skill;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public List<SkillVo> getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCasteLv() {
        return casteLv;
    }

    public void setCasteLv(int casteLv) {
        this.casteLv = casteLv;
    }

    public int getStarLv() {
        return starLv;
    }

    public void setStarLv(int starLv) {
        this.starLv = starLv;
    }
}
