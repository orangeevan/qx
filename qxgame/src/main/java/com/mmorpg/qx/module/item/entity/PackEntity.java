package com.mmorpg.qx.module.item.entity;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.Persister;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
@Cached(persister = @Persister("30s"))
public class PackEntity implements IEntity<Long> {

	@Id
	private long playerId;
	@Lob
	private String warehouse;
	@Lob
	private String equipmentStorage;
	@Lob
	private String troopStorage;

	@Override
	public Long getId() {
		return playerId;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getEquipmentStorage() {
		return equipmentStorage;
	}

	public void setEquipmentStorage(String equipmentStorage) {
		this.equipmentStorage = equipmentStorage;
	}

	public long getPlayerId() {
		return playerId;
	}

	public String getTroopStorage() {
		return troopStorage;
	}

	public void setTroopStorage(String troopStorage) {
		this.troopStorage = troopStorage;
	}

	@Override
	public boolean serialize() {
		return true;
	}
}
