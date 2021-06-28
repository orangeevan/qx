package com.mmorpg.qx.module.condition.resource;

import com.alibaba.fastjson.annotation.JSONField;
import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.condition.AbstractCondition;
import com.mmorpg.qx.module.condition.ConditionType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//@Resource
public class ConditionResource {

    private ConditionType type;
    /**
     * 条件唯一标志，同一条件唯一判断
     */
    private String code;

    private int value;

    private int min;

    private int max;
    /**
     * 条件参数
     */
    private String params;

    /**
     * 创建条件处理器
     *
     * @return
     */
    @JSONField(serialize = false)
    public AbstractCondition createConditon() {
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException("条件类型不能为空");
        }
        return type.create(this);
    }

    public int getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public ConditionType getType() {
        return type;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
