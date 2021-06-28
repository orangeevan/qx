package com.mmorpg.qx.module.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.common.moduletype.ModuleType;
import com.mmorpg.qx.common.moduletype.SubModuleType;
import com.mmorpg.qx.module.condition.ConditionManager;
import com.mmorpg.qx.module.condition.Conditions;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.consume.ConsumeManager;
import com.mmorpg.qx.module.consume.Consumes;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.reward.manager.RewardManager;
import com.mmorpg.qx.module.reward.model.Reward;
import com.mmorpg.qx.module.shop.manager.ShopManager;
import com.mmorpg.qx.module.shop.resource.ShopResource;

/**
 * 商场
 *
 * @author wang ke
 * @since v1.0 2018年3月21日
 */
@Component
public class ShopService {

    @Autowired
    private ShopManager shopManager;

    @Autowired
    private RewardManager rewardManager;

    public void shopBuy(Player player, int amount, int shopId, boolean quickBuy) {
        ShopResource shopResource = shopManager.getResource(shopId);

        // 2.判断条件
        Conditions conditions = ConditionManager.createConditions(shopResource.getConditionResources());
        Result result = conditions.verify(player, null, amount);
        if (!result.isSuccess()) {
            throw new ManagedException(result.getCode());
        }

        // 3.消耗检查
        Consumes consumes = ConsumeManager.createConsumes(shopResource.getConsumeResources());
        result = consumes.verify(player, amount);
        if (!result.isSuccess()) {
            throw new ManagedException(result.getCode());
        }

        // 4.消耗
        ModuleInfo moduleInfo = ModuleInfo.valueOf(ModuleType.SHOP, SubModuleType.SHOP_BUY, shopId + "");
        consumes.act(player, moduleInfo, amount);

        // 5.发奖
        Reward reward = null;
        if (quickBuy) {
            reward = RewardManager.creatReward(amount, shopResource.getQuickRewardResources());
        } else {
            reward = RewardManager.creatReward(amount, shopResource.getRewardResources());
        }
        rewardManager.grantReward(player, reward, moduleInfo);

    }
}
