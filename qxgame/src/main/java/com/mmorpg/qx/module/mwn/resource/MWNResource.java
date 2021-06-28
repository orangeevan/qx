package com.mmorpg.qx.module.mwn.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.mwn.enums.MwnQuality;
import com.mmorpg.qx.module.mwn.enums.MwnType;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrResource;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wang ke
 * @description: 魔物娘配置表
 * @since 11:21 2020-08-19
 */
@Resource
public class MWNResource extends AttrResource {
    @Id
    private int id;

    private String name;

    private int quality;

    private MwnQuality qualityType;

    private String baseAttrs;

    private transient List<Attr> baseAttrsList;

    private List<Integer> skill;

    /**
     * 生产技能
     */
    private List<Integer> workSkill;

    private int type;

    private int costMp;

    private int jobType;
    private int jobValue;
    private int eleType;
    private int eleValue;
    /**
     * 魔物娘进化技能
     */
    private List<Integer> skillEvolution;
    /**
     * 骰子消耗
     */
    private int diceCost;

    /**
     * 骰子点数
     */
    private int dicePoint;

    private List<Integer> gainableSkin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getBaseAttrs() {
        return baseAttrs;
    }

    public void setBaseAttrs(String baseAttrs) {
        this.baseAttrs = baseAttrs;
    }

    public List<Attr> getBaseAttrsList() {
        return new ArrayList<>(baseAttrsList);
    }

    public void setBaseAttrsList(List<Attr> baseAttrsList) {
        this.baseAttrsList = baseAttrsList;
    }

    public int getJobValue() {
        return jobValue;
    }

    public void setJobValue(int jobValue) {
        this.jobValue = jobValue;
    }


    public int getEleValue() {
        return eleValue;
    }

    public void setEleValue(int eleValue) {
        this.eleValue = eleValue;
    }

    public int getCostMp() {
        return costMp;
    }

    public void setCostMp(int costMp) {
        this.costMp = costMp;
    }

    public List<Integer> getSkill() {
        return skill;
    }

    public void setSkill(List<Integer> skill) {
        this.skill = skill;
    }

    public void initAttr() {
        baseAttrsList = GameUtil.buildAttr(this);
        baseAttrsList.add(Attr.valueOf(AttrType.valueOf(jobType), jobValue));
        baseAttrsList.add(Attr.valueOf(AttrType.valueOf(eleType), eleValue));
        qualityType = MwnQuality.valueOf(quality);
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public MwnQuality getQualityType() {
        return qualityType;
    }

    public void setQualityType(MwnQuality qualityType) {
        this.qualityType = qualityType;
    }

    public List<Integer> getWorkSkill() {
        return workSkill;
    }

    public void setWorkSkill(List<Integer> workSkill) {
        this.workSkill = workSkill;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getJobType() {
        return jobType;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }

    public int getEleType() {
        return eleType;
    }

    public void setEleType(int eleType) {
        this.eleType = eleType;
    }

    /**
     * 魔物娘能否随机
     *
     * @return
     */
    public boolean canRandom() {
        return MwnType.valueOf(type) == MwnType.Random;
    }

    public List<Integer> getSkillEvolution() {
        return skillEvolution;
    }

    public void setSkillEvolution(List<Integer> skillEvolution) {
        this.skillEvolution = skillEvolution;
    }

    /**
     * 进化前技能
     *
     * @return
     */
    public int getSkillEvoBefore() {
        if (CollectionUtils.isEmpty(skillEvolution)) {
            return 0;
        }
        return skillEvolution.get(0);
    }

    /**
     * 进化后技能
     *
     * @return
     */
    public int getSkillEvoAfter() {
        if (CollectionUtils.isEmpty(skillEvolution)) {
            return 0;
        }
        return skillEvolution.get(1);
    }

    public int getDiceCost() {
        return diceCost;
    }

    public void setDiceCost(int diceCost) {
        this.diceCost = diceCost;
    }

    public int getDicePoint() {
        return dicePoint;
    }

    public void setDicePoint(int dicePoint) {
        this.dicePoint = dicePoint;
    }

    public List<Integer> getGainableSkin() {
        return gainableSkin;
    }

    public void setGainableSkin(List<Integer> gainableSkin) {
        this.gainableSkin = gainableSkin;
    }
}
