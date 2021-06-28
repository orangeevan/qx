package com.mmorpg.qx.module.integral.reward;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.module.integral.service.IntegralService;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.reward.model.RewardItem;
import com.mmorpg.qx.module.reward.model.RewardProvider;
import com.mmorpg.qx.module.reward.model.RewardType;
import org.springframework.stereotype.Component;

/**
 * @author zhang peng
 * @description:
 * @since 9:48 2021/3/5
 */
@Component
public class IntegralRewardProvider extends RewardProvider {

    @Override
    public RewardType getType() {
        return RewardType.INTEGRAL;
    }

    @Override
    public void withdraw(Player player, RewardItem rewardItem, ModuleInfo module) {
        IntegralService integralService = BeanService.getBean(IntegralService.class);
        integralService.addIntegral(player, Integer.parseInt(rewardItem.getCode()), rewardItem.getAmount());
    }
}
