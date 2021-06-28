package com.mmorpg.qx.module.reward.manager;

import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.reward.model.Reward;
import com.mmorpg.qx.module.reward.model.RewardItem;
import com.mmorpg.qx.module.reward.model.RewardProvider;
import com.mmorpg.qx.module.reward.model.RewardType;
import com.mmorpg.qx.module.reward.resource.RewardResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author wang ke
 * @since v1.0 2018年3月20日
 *
 */
@Component
public class RewardManager {

	/** 奖励发放处理器 */
	private Map<RewardType, RewardProvider> providers = new HashMap<>();

	private static RewardManager self;

	public static RewardManager getInstance() {
		return self;
	}

	@PostConstruct
	public void init() {
		self = this;
	}

	public void registerProvider(RewardProvider rewardProvider) {
		providers.put(rewardProvider.getType(), rewardProvider);
	}

	public static Reward creatReward(int count, List<RewardResource> resources) {
		Reward reward = new Reward();
		for (RewardResource resource : resources) {
			reward.getRewardItems().add(resource.createRewardItem());
		}
		reward.multipleRewards(count);
		return reward;
	}

	public void grantReward(Player player, Reward reward, ModuleInfo module) {
//		List<RewardItem> itemRewardItems = New.arrayList();
//		List<RewardItem> notItemRewardItems = New.arrayList();
//		for (RewardItem item : reward.getRewardItems()) {
//			if (item.getType() == RewardType.ITEM) {
//				itemRewardItems.add(item);
//			} else {
//				notItemRewardItems.add(item);
//			}
//		}
//
//		for (RewardItem item : notItemRewardItems) {
//			this.providers.get(item.getType()).withdraw(player, item, module);
//		}
//		int needEmpty = 0;
//		for (RewardItem item : itemRewardItems) {
//			needEmpty += itemManager.calcOverlyCount(Integer.parseInt(item.getCode()), item.getAmount());
//		}

//		if (player.getBackpack().getEmptySize() < needEmpty && needEmpty != 0) {
//			// 如果背包装不下就发邮件
//			 Reward itemReward = Reward.valueOf();
//			 for (RewardItem item : itemRewardItems) {
//			 	itemReward.addRewardItem(item);
//			 }
//			 for (Reward spilited :	 itemReward.spilteItemReward(MAIL_MAX_ITEMS.getValue())) {
//				 I18nUtils i18nTile = I18nUtils.valueOf("PACK_full_mail_title");
//				 I18nUtils i18nContext = I18nUtils.valueOf("PACK_full_mail_content");
//				 String modulName = null;
//				 if (MODULE_NAME != null) {
//				 modulName = MODULE_NAME.getValue().get(module.getModule() + "");
//				 }
//				 i18nContext.addParm("moduleName", I18nPack.valueOf(modulName != null ?
//				 modulName : module.getModule()));
//				 Mail mail = Mail.valueOf(i18nTile, i18nContext, null, spilited);
//				 MailManager.getInstance().sendMail(mail, player.getObjectId());
//			 }
//		} else {
//			for (RewardItem item : itemRewardItems) {
//				this.providers.get(item.getType()).withdraw(player, item, module);
//			}
//		}

		for (RewardItem item : reward.getRewardItems()) {
			this.providers.get(item.getType()).withdraw(player, item, module);
		}
	}

}
