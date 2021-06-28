package com.mmorpg.qx.module.roundFight.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.packet.vo.AttrVo;
import com.mmorpg.qx.module.skill.packet.vo.SkillVo;

import java.util.List;

/**
 * @author wang ke
 * @description: 魔物娘信息
 * @since 16:42 2020-08-28
 */
public class MWNInfo {
    @Protobuf
    private long id;

    @Protobuf(description = "魔物娘配置表id")
    private int resourceId;

    @Protobuf(description = "技能信息")
    private List<SkillVo> skills;

    @Protobuf(description = "属性")
    private List<AttrVo> attrs;
//装备已经换算成属性
//    @Protobuf(description = "装备配置")
//    private List<Integer> equips;

    @Protobuf(description = "生命值")
    private int hp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public List<SkillVo> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillVo> skills) {
        this.skills = skills;
    }

    public List<AttrVo> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<AttrVo> attrs) {
        this.attrs = attrs;
    }

//    public List<Integer> getEquips() {
//        return equips;
//    }
//
//    public void setEquips(List<Integer> equips) {
//        this.equips = equips;
//    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public static MWNInfo valueOf(MWNCreature creature) {
        MWNInfo mwnInfo = new MWNInfo();
        mwnInfo.setId(creature.getObjectId());
        mwnInfo.setResourceId(creature.getResourceId());
        mwnInfo.setAttrs(creature.getAttrController().getAttrVos());
        mwnInfo.setHp(creature.getCurrentHp());
        mwnInfo.setSkills(creature.getSkillController().getSkillVo());
        return mwnInfo;
    }
}
