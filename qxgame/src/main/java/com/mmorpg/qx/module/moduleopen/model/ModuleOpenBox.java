package com.mmorpg.qx.module.moduleopen.model;

import java.util.Set;

import javax.persistence.Transient;

import com.mmorpg.qx.module.moduleopen.entity.ModuleOpenEntity;

public class ModuleOpenBox {

	@Transient
	private ModuleOpenEntity moduleOpenEntity;
	/**
	 * 已经开启的模块
	 */
	private Set<Integer> opened;

	public ModuleOpenEntity getModuleOpenEntity() {
		return moduleOpenEntity;
	}

	public void setModuleOpenEntity(ModuleOpenEntity moduleOpenEntity) {
		this.moduleOpenEntity = moduleOpenEntity;
	}

	public Set<Integer> getOpened() {
		return opened;
	}

	public void setOpened(Set<Integer> opened) {
		this.opened = opened;
	}



}
