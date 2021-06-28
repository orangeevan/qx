package com.mmorpg.qx.module.account.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * 登陆验证
 * 
 * @author wang ke
 * @since v1.0 2018年2月23日
 *
 */
@SocketPacket(packetId = PacketId.LOGIN_AUTH_REQ)
public class LoginAuthReq {
	@Protobuf(description = "账号")
	private String account;
	@Protobuf(description = "服务器id")
	private int serverId;
	@Protobuf(description = "验证码")
	private String md5check;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getMd5check() {
		return md5check;
	}

	public void setMd5check(String md5check) {
		this.md5check = md5check;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

}
