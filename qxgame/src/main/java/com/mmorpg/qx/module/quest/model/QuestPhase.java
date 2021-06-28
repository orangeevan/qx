package com.mmorpg.qx.module.quest.model;

/**
 * 
 * 
 * @author wang ke
 * @since v1.0 2017年1月3日
 * 
 */
public enum QuestPhase {

	/** 进行中 */
	PROGRESS((byte) 0),
	/** 完成 */
	COMPLETE((byte) 1);

	private byte value;

	private QuestPhase(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

}
