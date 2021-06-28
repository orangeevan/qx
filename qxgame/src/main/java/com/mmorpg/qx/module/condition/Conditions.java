package com.mmorpg.qx.module.condition;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/***
 * 条件组，设计目的同类条件或验证参数类似多个条件组合，不符合这种特征的条件最好用分配多个条件组，以免验证复杂
 * 条件配置跟业务关联，验证参数根据不同业务模块规划管理
 */
public class Conditions {

    private List<AbstractCondition> conditionList = new ArrayList<AbstractCondition>();

    @SuppressWarnings("unchecked")
    public <T extends AbstractCondition> List<T> findConditionType(Class<T> clazz) {
        List<T> conditions = new ArrayList<>();
        for (AbstractCondition abCondition : conditionList) {
            if (abCondition.getClass() == clazz || clazz.isAssignableFrom(abCondition.getClass())) {
                conditions.add((T) abCondition);
            }
        }
        return conditions;
    }

    public List<AbstractCondition> findConditions(ConditionType type) {
        return conditionList.stream().filter(condition -> condition.getType() == type).collect(Collectors.toList());
    }

    public Result verify(Object param1, Object param2, int amount) {
        for (AbstractCondition condition : conditionList) {
            Result result = condition.verify(param1, param2, amount);
            if (!result.isSuccess()) {
                return result;
            }
        }
        return Result.SUCCESS;
    }


    /***
     * 验证某类条件
     * @param type
     * @param param1
     * @param param2
     * @param amount
     * @return
     */
    public Result verify(ConditionType type, Object param1, Object param2, int amount) {
        List<AbstractCondition> typeConditions = findConditions(type);
        if (CollectionUtils.isEmpty(typeConditions)) {
            return Result.SUCCESS;
        }
        for (AbstractCondition condition : typeConditions) {
            if (condition.verify(param1, param2, amount) == Result.FAILURE) {
                return Result.FAILURE;
            }
        }
        return Result.SUCCESS;
    }

    public boolean verifyOr(Object param1, Object param2, int amount) {
        for (AbstractCondition condition : conditionList) {
            Result result = condition.verify(param1, param2, amount);
            if (result.isSuccess()) {
                return true;
            }
        }
        return false;
    }

    public void addCondition(AbstractCondition condition) {
        AbstractCondition add = condition;
        for (AbstractCondition temp : conditionList) {
            add = temp.add(add);
            if (add == null) {
                break;
            }
        }
        if (add != null) {
            conditionList.add(add);
        }
    }

    public void addCondition(Conditions conditions) {
        for (AbstractCondition add : conditions.conditionList) {
            addCondition(add);
        }
    }

    public void addConditions(AbstractCondition... conditions) {
        for (AbstractCondition condition : conditions) {
            addCondition(condition);
        }
    }

    public List<AbstractCondition> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<AbstractCondition> conditionList) {
        this.conditionList = conditionList;
    }

}
