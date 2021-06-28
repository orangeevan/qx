package com.mmorpg.qx.module.item.packet.resp;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.item.model.PackItem;

import java.util.List;

/**
 * 背包的更新
 *
 * @author wang ke
 * @since v1.0 2018年3月21日
 *
 */
@SocketPacket(packetId = PacketId.PACK_UPDATE_RESP)
public class BackPackUpdateResp {

	@Protobuf(description = "更新原因：0初始化 1使用道具 2获得道具 3道具过期")
	private int reason;
	@Protobuf(description = "1仓库, 2宝箱仓库")
	private int packType;
	@Protobuf(description = "更新后的道具")
	private List<PackItem> packItems;

	public static BackPackUpdateResp valueOf(List<PackItem> items, int packType, int reason) {
		BackPackUpdateResp resp = new BackPackUpdateResp();
		resp.setPackItems(items);
		resp.setPackType(packType);
		resp.setReason(reason);
		return resp;
	}

	public int getReason() {
		return reason;
	}

	public void setReason(int reason) {
		this.reason = reason;
	}

	public int getPackType() {
		return packType;
	}

	public void setPackType(int packType) {
		this.packType = packType;
	}

	public List<PackItem> getPackItems() {
		return packItems;
	}

	public void setPackItems(List<PackItem> packItems) {
		this.packItems = packItems;
	}
}
