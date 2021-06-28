package com.mmorpg.qx.module.condition;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author wang ke
 * @description: 加强条件配置解析
 * @since 10:55 2020-10-27
 */
public class StrengthCondConfig {
    /**
     * 加强条件
     */
    private String strengthCondition;

    private transient Conditions strenConditions;

    /**
     * 加强参数
     */
    private int strength;

    /**
     * 1：万分比比例系数
     * 2：纯数值变化
     */
    private int strengthValueType;

    public String getStrengthCondition() {
        return strengthCondition;
    }

    public void setStrengthCondition(String strengthCondition) {
        this.strengthCondition = strengthCondition;
    }

    public Conditions getStrenConditions() {
        return strenConditions;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getStrengthValueType() {
        return strengthValueType;
    }

    public void setStrengthValueType(int strengthValueType) {
        this.strengthValueType = strengthValueType;
    }

    public Conditions getAndCreateStrenConditions() {
        if (strenConditions == null) {
            if (StringUtils.isEmpty(strengthCondition)) {
                strenConditions = new Conditions();
            } else {
                List<ConditionResource> conditionResources = JsonUtils.string2List(strengthCondition, ConditionResource.class);
                strenConditions = ConditionManager.createConditions(conditionResources);
            }
        }
        return strenConditions;
    }

    public float getStrength() {
        if (isStrengthValueRate()) {
            return strength * 1.0f / 10000;
        }
        return strength;
    }

    public boolean isStrengthValueRate() {
        return this.strengthValueType == 1;
    }
}
