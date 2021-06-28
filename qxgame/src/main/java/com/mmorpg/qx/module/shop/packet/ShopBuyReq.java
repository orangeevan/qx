package com.mmorpg.qx.module.shop.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

@SocketPacket(packetId = PacketId.SHOP_BUY_REQ)
public class ShopBuyReq {

	@Protobuf
	private boolean quickBuy;
	@Protobuf
	private int shopId;
	@Protobuf
	private int amount;

	public boolean isQuickBuy() {
		return quickBuy;
	}

	public void setQuickBuy(boolean quickBuy) {
		this.quickBuy = quickBuy;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
