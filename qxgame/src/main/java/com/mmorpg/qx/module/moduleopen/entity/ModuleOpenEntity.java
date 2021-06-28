package com.mmorpg.qx.module.moduleopen.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.Persister;

@Entity
@Cached(persister = @Persister("30s"))
public class ModuleOpenEntity implements IEntity<Long> {

	@Id
	private long playerId;

	@Lob
	private String moduleOpen;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	@Override
	public Long getId() {
		return playerId;
	}

	public String getModuleOpen() {
		return moduleOpen;
	}

	public void setModuleOpen(String moduleOpen) {
		this.moduleOpen = moduleOpen;
	}

	@Override
	public boolean serialize() {
		// TODO Auto-generated method stub
		return false;
	}

}
