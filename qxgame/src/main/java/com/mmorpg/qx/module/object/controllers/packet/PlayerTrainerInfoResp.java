package com.mmorpg.qx.module.object.controllers.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.object.gameobject.packet.vo.AttrVo;
import com.mmorpg.qx.module.skill.packet.vo.EffectVo;
import com.mmorpg.qx.module.skill.packet.vo.SkillVo;
import java.util.List;

/**
 * 玩家驯养师信息
 *
 * @author wang ke
 * @since v1.0 2020年8月17日
 */
@SocketPacket(packetId = PacketId.PLAYER_TRAINER_INFO_RESP)
public class PlayerTrainerInfoResp {
    @Protobuf(description = "玩家角色id")
    private long playerId;

    @Protobuf(description = "驯养师自身id")
    private long id;

    @Protobuf(description = "驯养师模型id")
    private int resourceId;

    @Protobuf(description = "皮肤id")
    private int skinId;

    @Protobuf(description = "玩家姓名")
    private String name;

    @Protobuf(description = "职业")
    private int role;

    @Protobuf(description = "性别")
    private int sex;

    @Protobuf(description = "所在格子")
    private int gridId;

    @Protobuf(description = "朝向")
    private int dirType;

    @Protobuf
    private int curHp;

    @Protobuf
    private int curMp;

    @Protobuf
    private int gold;

    @Protobuf(description = "属性")
    private List<AttrVo> attrs;

    @Protobuf(description = "技能")
    private List<SkillVo> skills;

    @Protobuf(description = "buff效果")
    private List<EffectVo> effects;

    @Protobuf(description = "当前房间操作者ID")
    private long firstOp = 0L;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }

    public int getDirType() {
        return dirType;
    }

    public void setDirType(int dirType) {
        this.dirType = dirType;
    }

    public List<AttrVo> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<AttrVo> attrs) {
        this.attrs = attrs;
    }

    public List<SkillVo> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillVo> skills) {
        this.skills = skills;
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

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getSkinId() {
        return skinId;
    }

    public void setSkinId(int skinId) {
        this.skinId = skinId;
    }

    public List<EffectVo> getEffects() {
        return effects;
    }

    public void setEffects(List<EffectVo> effects) {
        this.effects = effects;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFirstOp() {
        return firstOp;
    }

    public void setFirstOp(long firstOp) {
        this.firstOp = firstOp;
    }
}
