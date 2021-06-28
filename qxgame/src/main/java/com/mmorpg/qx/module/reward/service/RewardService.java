package com.mmorpg.qx.module.reward.service;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.reward.manager.RewardManager;
import com.mmorpg.qx.module.reward.model.Reward;
import com.mmorpg.qx.module.reward.model.RewardItem;
import com.mmorpg.qx.module.reward.model.RewardType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhang peng
 * @description
 * @since 19:24 2021/4/26
 */
@Component
public class RewardService {

    @Autowired
    private RewardManager rewardManager;

    public static RewardService getInstance() {
        return BeanService.getBean(RewardService.class);
    }

    /**
     * 获取奖励
     *
     * @param player
     * @param rewardType  奖励类型
     * @param code        奖励编码
     * @param num         奖励数量
     * @param moduleInfo
     */
    public void grantReward(Player player, RewardType rewardType, String code, int num, ModuleInfo moduleInfo) {
        Reward reward = new Reward();
        RewardItem rewardItem = RewardItem.valueOf(rewardType, code, num, null);
        reward.getRewardItems().add(rewardItem);
        rewardManager.grantReward(player, reward, moduleInfo);
    }
}
