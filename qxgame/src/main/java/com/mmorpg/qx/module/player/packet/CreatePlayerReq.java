package com.mmorpg.qx.module.player.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

@SocketPacket(packetId = PacketId.CREATE_PLAYER_REQ)
public class CreatePlayerReq {
	@Protobuf(description = "角色姓名")
	private String name;
	@Protobuf(description = "职业")
	private int role;
	@Protobuf(description = "性别.男-1,女-2")
	private int sex;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

}
