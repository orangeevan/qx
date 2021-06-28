package com.mmorpg.qx.module.moduleopen.resource;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmorpg.qx.common.moduletype.ModuleType;
import com.mmorpg.qx.module.condition.ConditionManager;
import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

@Resource
public class ModuleOpenResource {
	@Id
	private int id;

	/**
	 * 关卡类型
	 */
	private int type;

	/**
	 * 开启等级
	 */
	private int level;

	/**
	 * 关卡进度
	 */
	private int barrier;

	/**
	 * 模块类型
	 */
	private ModuleType moType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getBarrier() {
		return barrier;
	}

	public void setBarrier(int barrier) {
		this.barrier = barrier;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ModuleType getMoType() {
		return moType;
	}

	public void setMoType(ModuleType moType) {
		this.moType = moType;
	}
}
