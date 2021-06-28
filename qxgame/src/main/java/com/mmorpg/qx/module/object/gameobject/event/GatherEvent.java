package com.mmorpg.qx.module.object.gameobject.event;

import com.haipaite.common.event.event.IEvent;

public class GatherEvent implements IEvent {

	private long owner;

	private String key;

	public static GatherEvent valueOf(long owner, String key) {
		GatherEvent mke = new GatherEvent();
		mke.owner = owner;
		mke.setKey(key);
		return mke;
	}

	public long getOwner() {
		return this.owner;
	}

	public void setOwner(long owner) {
		this.owner = owner;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
