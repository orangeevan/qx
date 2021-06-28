package com.mmorpg.qx.module.object.resource;

import java.util.List;
import com.mmorpg.qx.module.ai.aitype.AIType;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.reward.model.Reward;
import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 * 这个配置表是生物的全量配置表，包含了所有类别的生物所需要的属性集合，但是会由生物管理器根据不同的类别来创建 原理类似与物品管理器
 *
 * @author wang ke
 * @since v1.0 2018年2月24日
 */
@Resource
public class ObjectResource {
    @Id
    private int key;
    /**
     * 姓名
     */
    private String name;
    /**
     * 生物类型
     */
    private ObjectType objectType;
    /**
     * 杀死奖励
     */
    private Reward reward;

    /**
     * 属性
     */
    private String attrs;

    private List<Attr> attrsList;

    /**
     * 默认使用技能id
     */
    private int skill;

    /**
     * 等级
     */
    private int level;

    /**
     * ai类型
     **/
    private AIType aiType;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(String attrs) {
        this.attrs = attrs;
    }

    public List<Attr> getAttrsList() {
        return attrsList;
    }

    public void setAttrsList(List<Attr> attrsList) {
        this.attrsList = attrsList;
    }

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public AIType getAiType() {
        return aiType;
    }

    public void setAiType(AIType aiType) {
        this.aiType = aiType;
    }
}
