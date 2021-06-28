package com.mmorpg.qx.module.quest.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.quest.packet.vo.PairEntry;
import com.mmorpg.qx.module.quest.packet.vo.QuestVO;

import java.util.List;

/**
 * 任务刷新
 * 
 * @author wang ke
 * @since v1.0 2017年1月4日
 * 
 */
@SocketPacket(packetId = PacketId.QUEST_UPDATE_RESP)
public class QuestUpdateResp {
	/** 当前任务列表 */
	@Protobuf
	private List<QuestVO> currentQuests;
	/** 任务移除列表 */
	@Protobuf
	private List<Integer> removes;
	/** 客户端可接任务列表 */
	@Protobuf
	private List<Integer> clientAccepts;
	/** 历史完成的任务记录 */
	@Protobuf
	private List<PairEntry> completionHistory;
	/** 今天完成的任务记录 */
	@Protobuf
	private List<PairEntry> todayCompletionHistory;

	public List<QuestVO> getCurrentQuests() {
		return currentQuests;
	}

	public void setCurrentQuests(List<QuestVO> currentQuests) {
		this.currentQuests = currentQuests;
	}

	public List<Integer> getRemoves() {
		return removes;
	}

	public void setRemoves(List<Integer> removes) {
		this.removes = removes;
	}

	public List<Integer> getClientAccepts() {
		return clientAccepts;
	}

	public void setClientAccepts(List<Integer> clientAccepts) {
		this.clientAccepts = clientAccepts;
	}

	public List<PairEntry> getCompletionHistory() {
		return completionHistory;
	}

	public void setCompletionHistory(List<PairEntry> completionHistory) {
		this.completionHistory = completionHistory;
	}

	public List<PairEntry> getTodayCompletionHistory() {
		return todayCompletionHistory;
	}

	public void setTodayCompletionHistory(List<PairEntry> todayCompletionHistory) {
		this.todayCompletionHistory = todayCompletionHistory;
	}

}
