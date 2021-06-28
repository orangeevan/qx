package com.mmorpg.qx.module.quest.resource;

import java.util.List;

import javax.persistence.Transient;

import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.condition.impl.player.PlayerLevelCondition;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.mmorpg.qx.module.quest.model.QuestType;
import com.mmorpg.qx.module.quest.target.QuestTargetDef;
import com.mmorpg.qx.module.reward.resource.RewardResource;
import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 *
 * 任务配置表
 *
 * @author wang ke
 * @since v1.0 2017年1月3日
 *
 */
@Resource
public class QuestResource {

	/** 任务id */
	@Id
	private int id;
	/** 任务类型{@QuestType.getValue} */
	private QuestType questType;
	/** 接取任务的最低等级限制 */
	private int minLevel;
	/** 接取条件配置 */
	private List<ConditionResource> acceptConditionDefs;
	/** 前置任务完成限制 */
	private List<Integer> needQuests;
	/** 是否自动接取 */
	private boolean autoAccept;
	/** 是否客戶端操作完成(比如打开一个面板,纯客戶端操作類的任务) */
	private boolean clientComplete;
	/** 任务需要达成目标 */
	private List<QuestTargetDef> targetDefs;
	/** 删除条件 */
	private List<ConditionResource> removeConditionDefs;
	/** 完成任务后，判定接取子任务 */
	private List<Integer> childQuests;
	/** 完成后奖励 */
	private List<RewardResource> rewards;
	/** 自动发奖 */
	private boolean autoReward;
	/** 接取任务的NPC */
	private int acceptNPC;
	/** 领取奖励的NPC */
	private int commitNPC;
	/** 完成任务触发相应模块Id */
	private int moduleId;

	public static void main(String[] args) {
//		RewardResource resource = new RewardResource();
//		resource.setType(RewardType.CURRENCY);
//		resource.setAmount(100);
//		resource.setCode(CurrencyType.COPPER.name());
//		List<RewardResource> resources = new ArrayList<>();
//		resources.add(resource);
//		SystemOut.println(JsonUtils.object2String(resources));
		/*
		Reward reward = Reward.valueOf();
		RewardItem item = RewardItem.valueOf(RewardType.ITEM, "222", 11);
		reward.addRewardItem(item);
		String json = JSON.toJSONString(reward);
		Reward newReward = JSON.parseObject(json, new TypeReference<Reward>() {
		});
		System.out.println(JSON.toJSONString(newReward));
		
		QuestTargetDef def = new QuestTargetDef();
		def.setType(QuestTargetType.MONSTER_HUNT);
		def.setValue(10);
		def.getParms().put("MONSTERID", "200101");
		List<QuestTargetDef> defs = new ArrayList<>();
		defs.add(def);
		System.out.println(JSON.toJSONString(defs));
		
		List<ConditionDef> conditions = new ArrayList<>();
		ConditionDef cdef = new ConditionDef();
		cdef.setType(ConditionType.DAY_AFTER_OPENSERVER);
		conditions.add(cdef);
		String conditionJson = JSON.toJSONString(conditions);
		System.out.println(conditionJson);
		// List<ConditionDef> newConditions =
		// JSON.parseObject("[{'questId':'2','type':'DAY_AFTER_OPENSERVER'}]",
		// new TypeReference<List<ConditionDef>>() {
		// });
		System.out.println("[{'questId':'2','type':'DAY_AFTER_OPENSERVER'}]"
				.equals("[{'questId':'2','type':'DAY_AFTER_OPENSERVER​'}]"));
		String str1 = new String("DAY_AFTER_OPENSERVER");
		String str2 = new String("DAY_AFTER_OPENSERVE");
		String str3 = new String("DAY_AFTER_OPENSERVE");
		System.out.println(str1.length());
		System.out.println(str2.length());
		System.out.println(str3.length());
		System.out.println(str1.equals(str2));
		System.out.println(ConditionType.DAY_AFTER_OPENSERVER.name().length());
		System.out.println("LEVEL".equals("LEVEL"));
		System.out.println("[{'taskId':'1000032','type':'PLAYER_TASK'}]");
		
		List<ConditionDef> newConditions = JSON.parseObject("[{'taskId':'1000032','type':'PLAYER_TASK'}]",
				new TypeReference<List<ConditionDef>>() {
				});
		// List<ConditionDef> newConditions =
		// JSON.parseArray("[{'day':'2','type':'DAY_AFTER_OPENSERVER​'}]",
		// ConditionDef.class);
		// List<ConditionDef> newConditions =
		// JSON.parseArray("[{'quest':'2','type':'QUEST_COMPLETE​'}]",
		// ConditionDef.class);
		System.out.println(JSON.toJSONString(newConditions));
		
		System.out.println(JSON.toJSONString("12,22,22".split(",")));
		
		 */
	}

	@Transient
	private Conditions acceptCondition;

	@Transient
	public Conditions removeCondition;

	public Conditions getAcceptCondition() {
		if (acceptCondition == null) {
			acceptCondition = new Conditions();
			if (minLevel != 0) {
				PlayerLevelCondition condition = new PlayerLevelCondition();
				condition.setLow(minLevel);
				condition.setHigh(Integer.MAX_VALUE);
				acceptCondition.addCondition(condition);
			}
			if (acceptConditionDefs != null) {
				for (ConditionResource conditionDef : acceptConditionDefs) {
					acceptCondition.addCondition(conditionDef.getType().create(conditionDef));
				}
			}
			//acceptCondition.addCondition(getQuestType().createConditions(this));
		}

		return acceptCondition;
	}

	public Conditions getRemoveCondition() {
		if (removeCondition == null) {
			removeCondition = new Conditions();
			if (removeConditionDefs != null) {
				for (ConditionResource resource : removeConditionDefs) {
					removeCondition.addCondition(resource.createConditon());
				}
			}
		}
		return removeCondition;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public QuestType getQuestType() {
		return questType;
	}

	public void setQuestType(QuestType questType) {
		this.questType = questType;
	}

	public List<QuestTargetDef> getTargetDefs() {
		return targetDefs;
	}

	public void setTargetDefs(List<QuestTargetDef> targetDefs) {
		this.targetDefs = targetDefs;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public List<ConditionResource> getAcceptConditionDefs() {
		return acceptConditionDefs;
	}

	public void setAcceptConditionDefs(List<ConditionResource> acceptConditionDefs) {
		this.acceptConditionDefs = acceptConditionDefs;
	}

	public List<Integer> getNeedQuests() {
		return needQuests;
	}

	public void setNeedQuests(List<Integer> needQuests) {
		this.needQuests = needQuests;
	}

	public boolean isAutoAccept() {
		return autoAccept;
	}

	public void setAutoAccept(boolean autoAccept) {
		this.autoAccept = autoAccept;
	}

	public boolean isClientComplete() {
		return clientComplete;
	}

	public void setClientComplete(boolean clientComplete) {
		this.clientComplete = clientComplete;
	}

	public List<ConditionResource> getRemoveConditionDefs() {
		return removeConditionDefs;
	}

	public void setRemoveConditionDefs(List<ConditionResource> removeConditionDefs) {
		this.removeConditionDefs = removeConditionDefs;
	}

	public List<Integer> getChildQuests() {
		return childQuests;
	}

	public void setChildQuests(List<Integer> childQuests) {
		this.childQuests = childQuests;
	}

	public boolean isAutoReward() {
		return autoReward;
	}

	public void setAutoReward(boolean autoReward) {
		this.autoReward = autoReward;
	}

	public int getAcceptNPC() {
		return acceptNPC;
	}

	public void setAcceptNPC(int acceptNPC) {
		this.acceptNPC = acceptNPC;
	}

	public int getCommitNPC() {
		return commitNPC;
	}

	public void setCommitNPC(int commitNPC) {
		this.commitNPC = commitNPC;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public void setAcceptCondition(Conditions acceptCondition) {
		this.acceptCondition = acceptCondition;
	}

	public void setRemoveCondition(Conditions removeCondition) {
		this.removeCondition = removeCondition;
	}

	public List<RewardResource> getRewards() {
		return rewards;
	}

	public void setRewards(List<RewardResource> rewards) {
		this.rewards = rewards;
	}

}
