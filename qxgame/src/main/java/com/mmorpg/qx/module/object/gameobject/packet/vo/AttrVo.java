package com.mmorpg.qx.module.object.gameobject.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class AttrVo {
	@Protobuf
	private int attrEnum;
	@Protobuf
	private int value;

	public static AttrVo valueOf(int attrEnum, int value) {
		AttrVo vo = new AttrVo();
		vo.attrEnum = attrEnum;
		vo.value = value;
		return vo;
	}

	public int getAttrEnum() {
		return attrEnum;
	}

	public void setAttrEnum(int attrEnum) {
		this.attrEnum = attrEnum;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "AttrVo{" +
				"attrEnum=" + attrEnum +
				", value=" + value +
				'}';
	}
}
