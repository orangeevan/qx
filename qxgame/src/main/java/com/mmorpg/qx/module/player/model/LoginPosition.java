package com.mmorpg.qx.module.player.model;

/**
 * 登陆处理器,确定玩家应该进入哪里
 * @author wang ke
 * @since v1.0 2019年3月6日
 *
 */
public class LoginPosition {

	/**
	 * 响应的处理器
	 */
	private LoginPositionType type;
	/** 登陆信息 */
	private int gridId;
	private int mapId;
	private int instanceId;

	public LoginPosition(LoginPositionType type) {
		this.type = type;
	}

	public LoginPositionType getType() {
		return type;
	}

	public void setType(LoginPositionType type) {
		this.type = type;
	}

	public int getGridId() {
		return gridId;
	}

	public void setGridId(int gridId) {
		this.gridId = gridId;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

}
