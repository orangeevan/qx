package com.mmorpg.qx.module.account.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * 登陆验证结果
 * 
 * @author wang ke
 * @since v1.0 2018年2月23日
 *
 */
@SocketPacket(packetId = PacketId.LOGIN_AUTH_RESP)
public class LoginAuthResp {

	@Protobuf(description = "0 正常登陆")
	private int result;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
