package com.mmorpg.qx.module.quest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.Persister;

@Entity
@Cached(persister = @Persister("30s"))
public class QuestEntity implements IEntity<Long> {
	@Id
	private long playerId;

	@Lob
	private String acceptableQuestIds;

	@Lob
	private String acceptedQuests;

	@Lob
	private String completedCounts;

	@Lob
	private String todayCompletedCounts;

	/**
	 * 记录当前的接取到的主线任务Id,便于日志读取
	 */
	@Column(columnDefinition = "int NOT NULL DEFAULT 0")
	private int trunkQuestId;

	private long lastRefreshDailyQuestTime;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public String getAcceptableQuestIds() {
		return acceptableQuestIds;
	}

	public void setAcceptableQuestIds(String acceptableQuestIds) {
		this.acceptableQuestIds = acceptableQuestIds;
	}

	public String getAcceptedQuests() {
		return acceptedQuests;
	}

	public void setAcceptedQuests(String acceptedQuests) {
		this.acceptedQuests = acceptedQuests;
	}

	public String getCompletedCounts() {
		return completedCounts;
	}

	public void setCompletedCounts(String completedCounts) {
		this.completedCounts = completedCounts;
	}

	public String getTodayCompletedCounts() {
		return todayCompletedCounts;
	}

	public void setTodayCompletedCounts(String todayCompletedCounts) {
		this.todayCompletedCounts = todayCompletedCounts;
	}

	public long getLastRefreshDailyQuestTime() {
		return lastRefreshDailyQuestTime;
	}

	public void setLastRefreshDailyQuestTime(long lastRefreshDailyQuestTime) {
		this.lastRefreshDailyQuestTime = lastRefreshDailyQuestTime;
	}

	public int getTrunkQuestId() {
		return trunkQuestId;
	}

	public void setTrunkQuestId(int trunkQuestId) {
		this.trunkQuestId = trunkQuestId;
	}

	@Override
	public Long getId() {
		return playerId;
	}

	@Override
	public boolean serialize() {
		return true;
	}

}
