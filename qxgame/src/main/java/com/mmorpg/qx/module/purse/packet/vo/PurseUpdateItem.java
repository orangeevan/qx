package com.mmorpg.qx.module.purse.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class PurseUpdateItem {
	@Protobuf(description = "提纯氪金(1), 氪金(2), 金币(3)")
	private int type;
	@Protobuf
	private long value;

	public static PurseUpdateItem valueOf(int type, long value) {
		PurseUpdateItem resp = new PurseUpdateItem();
		resp.type = type;
		resp.value = value;
		return resp;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

}
