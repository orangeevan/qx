package com.mmorpg.qx.module.quest.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class QuestTargetVO {
	@Protobuf(description = "任务目标类型")
	private int type;
	@Protobuf(description = "任务进度")
	private int value;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
