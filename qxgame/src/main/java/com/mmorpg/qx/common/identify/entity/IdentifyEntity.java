package com.mmorpg.qx.common.identify.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.Persister;

/**
 * 唯一主键标识实体
 * @author wang ke
 * @since v1.0 2018年3月7日
 *
 */
@Entity
@Cached(persister = @Persister("10s"))
public class IdentifyEntity implements IEntity<String> {

	@Id
	@Column(length = 50)
	private String id;
	private long now;

	public static IdentifyEntity valueOf(String id, long value) {
		IdentifyEntity result = new IdentifyEntity();
		result.id = id;
		result.now = value;
		return result;
	}

	@Override
	public String getId() {
		return id;
	}

	public long getNextIdentify() {
		return ++now;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean serialize() {
		return true;
	}

}
