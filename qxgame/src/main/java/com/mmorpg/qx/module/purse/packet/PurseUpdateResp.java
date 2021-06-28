package com.mmorpg.qx.module.purse.packet;

import java.util.List;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.purse.packet.vo.PurseUpdateItem;

@SocketPacket(packetId = PacketId.PURSE_UPDATE_RESP)
public class PurseUpdateResp {

	@Protobuf
	private List<PurseUpdateItem> items;

	public static PurseUpdateResp valueOf(List<PurseUpdateItem> items) {
		PurseUpdateResp resp = new PurseUpdateResp();
		resp.items = items;
		return resp;
	}

	public List<PurseUpdateItem> getItems() {
		return items;
	}

	public void setItems(List<PurseUpdateItem> items) {
		this.items = items;
	}
}
