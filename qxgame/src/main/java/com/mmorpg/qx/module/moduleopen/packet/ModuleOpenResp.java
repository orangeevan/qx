package com.mmorpg.qx.module.moduleopen.packet;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

import java.util.List;

@SocketPacket(packetId = PacketId.MODULE_OPEN_RESP)
public class ModuleOpenResp {

	/**
	 * 开启模块
	 */
	@Protobuf(fieldType = FieldType.INT32, order = 0, required = false)
	private List<Integer> opens;

	public List<Integer> getOpens() {
		return opens;
	}

	public void setOpens(List<Integer> opens) {
		this.opens = opens;
	}
}
