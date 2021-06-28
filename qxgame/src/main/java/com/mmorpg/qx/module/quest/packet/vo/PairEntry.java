package com.mmorpg.qx.module.quest.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class PairEntry {
	@Protobuf
	private int questId;
	@Protobuf
	private int count;

	public static PairEntry valueOf(Integer q, Integer c) {
		PairEntry resp = new PairEntry();
		if (q == null) {
			resp.setQuestId(0);
		} else {
			resp.setQuestId(q);
		}
		if (c == null) {
			resp.setCount(0);
		} else {
			resp.setCount(c);
		}
		return resp;
	}

	public int getQuestId() {
		return questId;
	}

	public void setQuestId(int questId) {
		this.questId = questId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
