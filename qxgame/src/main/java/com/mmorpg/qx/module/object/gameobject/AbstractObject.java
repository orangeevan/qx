package com.mmorpg.qx.module.object.gameobject;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * 世界唯一对象
 * @author wang ke
 * @since v1.0 2018年3月7日
 *
 */
public abstract class AbstractObject {

	@Protobuf
	protected Long objectId;

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public abstract String getName();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractObject other = (AbstractObject) obj;
		if (objectId == null) {
			if (other.objectId != null) {
				return false;
			}
		} else if (!objectId.equals(other.objectId)) {
			return false;
		}
		return true;
	}

}
