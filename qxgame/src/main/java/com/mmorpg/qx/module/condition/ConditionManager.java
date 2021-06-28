package com.mmorpg.qx.module.condition;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.mmorpg.qx.common.SystemOut;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import org.springframework.util.CollectionUtils;

@Component
public class ConditionManager {

	private static ConditionManager self;

	public static ConditionManager getInstance() {
		return self;
	}

	@PostConstruct
	private void init() {
		self = this;
	}

	/*
	public Conditions getConditions(int size, String... keys) {
		Conditions Conditions = new Conditions();
		if (keys != null && keys.length > 0) {
			AbstractCondition[] conditions = new AbstractCondition[keys.length];
			for (int i = 0, j = keys.length; i < j; i++) {
				ConditionResource resource = ConditionResource.get(keys[i], true);
				conditions[i] = resource.getType().create();
				try {
					conditions[i].init(resource);
				} catch (Exception e) {
					SystemOut.systemOut(keys[i]);
				}
				conditions[i].calculate(size);
			}
			Conditions.addConditions(conditions);
		}
		return Conditions;
	}
	
	public ConditionResource[] getConditionResources(String... keys) {
		List<ConditionResource> resources = new ArrayList<ConditionResource>();
		if (keys != null && keys.length > 0) {
			for (int i = 0, j = keys.length; i < j; i++) {
				ConditionResource r = ConditionResource.get(keys[i], true);
				resources.add(r);
			}
		}
		int size = resources.size();
		return resources.toArray(new ConditionResource[size]);
	}
	
	public Conditions getConditions(KeyContext... contexts) {
		Conditions Conditions = new Conditions();
		if (contexts != null && contexts.length > 0) {
			AbstractCondition[] conditions = new AbstractCondition[contexts.length];
			for (int i = 0, j = contexts.length; i < j; i++) {
				ConditionResource resource = ConditionResource.get(contexts[i].getKey(), true);
				conditions[i] = resource.getType().create();
				conditions[i].init(resource);
				conditions[i].calculate(contexts[i].getMount());
			}
			Conditions.addConditions(conditions);
		}
		return Conditions;
	}
	
	public Conditions getConditions(ConditionResource... resources) {
		return getConditions(1, resources);
	}
	
	
	public Conditions getConditions(int size, ConditionResource... resources) {
		// 按道理说不应该传一个空的条件，不做空的验证是希望错误早一点报错，而不是一直通过验证
		Conditions Conditions = new Conditions();
		AbstractCondition[] conditions = new AbstractCondition[resources.length];
		for (int i = 0; i < resources.length; i++) {
			conditions[i] = resources[i].getType().create();
			conditions[i].init(resources[i]);
			// conditions[i].value = resources[i].calculateValue(null);
			conditions[i].calculate(size);
		}
		Conditions.addConditions(conditions);
		return Conditions;
	}
	
	public boolean isPass(Object object, String[] limitIds, Map<String, Object> limitCtx, boolean throwException) {
		Conditions Conditions = getConditions(1, limitIds);
		return Conditions.verify(object);
	}
	
	public Storage<String, ConditionResource> getConditionResource() {
		return ConditionResource;
	}
	*/

	public static Conditions createConditions(List<ConditionResource> resources) {
		Conditions conditions = new Conditions();
		if (CollectionUtils.isEmpty(resources)) {
			return conditions;
		}
		for (ConditionResource resource : resources) {
			conditions.addCondition(resource.createConditon());
		}
		return conditions;
	}
}
