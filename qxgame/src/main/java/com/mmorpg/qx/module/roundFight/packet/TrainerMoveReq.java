package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.worldMap.enums.DirType;

@SocketPacket(packetId = PacketId.TRAINER_MOVE_REQ)
public class TrainerMoveReq {

	@Protobuf(description = "请求移动步数")
	private int step;

	@Protobuf(description = "请求移动方向")
	private DirType dir ;

	@Protobuf(description = "玩家移动前所站格子编号")
	private int currGrid;

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public DirType getDir() {
		return dir;
	}

	public void setDir(DirType dir) {
		this.dir = dir;
	}

	public int getCurrGrid() {
		return currGrid;
	}

	public void setCurrGrid(int currGrid) {
		this.currGrid = currGrid;
	}
}
