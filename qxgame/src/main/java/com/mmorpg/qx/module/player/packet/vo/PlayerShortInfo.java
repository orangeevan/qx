package com.mmorpg.qx.module.player.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class PlayerShortInfo {
	@Protobuf(description = "角色id")
	private long playerId;
	@Protobuf(description = "账号")
	private String account;
	@Protobuf(description = "服务器id")
	private int serverId;
	@Protobuf(description = "角色名")
	private String name;
	@Protobuf(description = "职业（暂留）")
	private int role;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

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

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

}
