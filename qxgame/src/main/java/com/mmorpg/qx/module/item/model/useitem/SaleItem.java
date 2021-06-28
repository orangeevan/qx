package com.mmorpg.qx.module.item.model.useitem;

import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
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
 * @since 14:30 2021/4/27
 */
@Component
public class SaleItem extends AbstractUseItem {

    @Override
    public UseType getUseType() {
        return UseType.SALE;
    }

    @Override
    public GainType getGainType() {
        return GainType.NONE;
    }

    @Override
    public void use(Player player, ItemResource resource, long objectId, int num) {
        if (resource.getSaleType() == 0) {
            throw new ManagedException(ManagedErrorCode.ITEM_CAN_NOT_SALE);
        }
        // 消耗道具
        ItemService.getInstance().consumeItem(player, objectId, "item", num);

        // 获得货币
        RewardService.getInstance().grantReward(player, RewardType.PURSE, String.valueOf(resource.getSaleType()),
                resource.getSaleNum() * num,  ModuleInfo.valueOf(ModuleType.ITEM, SubModuleType.ITEM_SALE));
    }
}
