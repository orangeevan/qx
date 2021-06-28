package com.mmorpg.qx.module.quest.packet.vo;

import java.util.List;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * 任务
 * @author wang ke
 * @since v1.0 2018年3月28日
 *
 */
public class QuestVO {
	@Protobuf(description = "任务id")
	private int id;
	@Protobuf(description = "任务状态")
	private byte phase;
	@Protobuf(description = "创建时间 ")
	private long createTime;
	@Protobuf(description = "目标列表 ")
	private List<QuestTargetVO> targets;

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public byte getPhase() {
		return phase;
	}

	public void setPhase(byte phase) {
		this.phase = phase;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<QuestTargetVO> getTargets() {
		return targets;
	}

	public void setTargets(List<QuestTargetVO> targets) {
		this.targets = targets;
	}

}
