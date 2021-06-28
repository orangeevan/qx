package com.mmorpg.qx.module.item.model.useitem;

import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.common.moduletype.ModuleType;
import com.mmorpg.qx.common.moduletype.SubModuleType;
import com.mmorpg.qx.module.item.resource.ItemResource;
import com.mmorpg.qx.module.item.service.ItemService;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.reward.model.RewardType;
import com.mmorpg.qx.module.reward.service.RewardService;
import org.springframework.stereotype.Component;

/**
 * @author zhang peng
 * @description
 * @since 11:11 2021/4/27
 */
@Component
public class UseItemGainIntegral extends AbstractUseItem {

    @Override
    public UseType getUseType() {
        return UseType.USE;
    }

    @Override
    public GainType getGainType() {
        return GainType.INTEGRAL;
    }

    @Override
    public void use(Player player, ItemResource resource, long objectId, int num) {
        // 消耗道具
        ItemService.getInstance().consumeItem(player, objectId, "item", num);

        // 获得货币
        String code = String.valueOf(resource.getUseEffectId());
        int totalNum = resource.getUseEffectNum() * num;
        ModuleInfo moduleInfo = ModuleInfo.valueOf(ModuleType.ITEM, SubModuleType.ITEM_USE);
        RewardService.getInstance().grantReward(player, RewardType.INTEGRAL, code, totalNum, moduleInfo);
    }
}
