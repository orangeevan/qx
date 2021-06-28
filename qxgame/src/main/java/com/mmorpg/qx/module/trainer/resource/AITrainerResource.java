package com.mmorpg.qx.module.trainer.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.ai.aitype.AIType;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrResource;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;

import java.util.List;
import java.util.Map;

/**
 * @author wang ke
 * @description: 机器人驯养师配置
 * @since 10:48 2020-08-20
 */
@Resource
public class AITrainerResource extends AttrResource {
    @Id
    private int id;

    private String name;

    private int level;

    private List<Attr> baseAttrsList;

    /**
     * 卡牌
     */
    private Map<Integer, Integer> cards;

    private List<Integer> skill;

    private AttrType jobType;

    private int jobValue;

    private AttrType eleType;

    private int eleValue;

    private int aiTypeId;

    private AIType aiType;

    /**
     * 初始自动添加效果，比如boss初始自动休眠效果，满足一定条件后移除
     */
    private int initEffect;

    private int skinId;

    private int modelId;

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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Attr> getBaseAttrsList() {
        return baseAttrsList;
    }

    public void setBaseAttrsList(List<Attr> baseAttrsList) {
        this.baseAttrsList = baseAttrsList;
    }

    public Map<Integer, Integer> getCards() {
        return cards;
    }

    public void setCards(Map<Integer, Integer> cards) {
        this.cards = cards;
    }

    public List<Integer> getSkill() {
        return skill;
    }

    public void setSkill(List<Integer> skill) {
        this.skill = skill;
    }

    public AttrType getJobType() {
        return jobType;
    }

    public void setJobType(AttrType jobType) {
        this.jobType = jobType;
    }

    public int getJobValue() {
        return jobValue;
    }

    public void setJobValue(int jobValue) {
        this.jobValue = jobValue;
    }

    public AttrType getEleType() {
        return eleType;
    }

    public void setEleType(AttrType eleType) {
        this.eleType = eleType;
    }

    public int getEleValue() {
        return eleValue;
    }

    public void setEleValue(int eleValue) {
        this.eleValue = eleValue;
    }

    public int getAiTypeId() {
        return aiTypeId;
    }

    public void setAiTypeId(int aiTypeId) {
        this.aiTypeId = aiTypeId;
    }

    public AIType getAiType() {
        return aiType;
    }

    public int getInitEffect() {
        return initEffect;
    }

    public void setInitEffect(int initEffect) {
        this.initEffect = initEffect;
    }

    public int getSkinId() {
        return skinId;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public void setSkinId(int skinId) {
        this.skinId = skinId;
    }

    public void init() {
        baseAttrsList = GameUtil.buildAttr(this);
        baseAttrsList.add(Attr.valueOf(jobType, jobValue));
        baseAttrsList.add(Attr.valueOf(eleType, eleValue));
        aiType = AIType.valueOf(aiTypeId);
    }

}
