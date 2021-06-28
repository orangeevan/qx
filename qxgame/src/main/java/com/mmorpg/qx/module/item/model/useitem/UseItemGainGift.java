package com.mmorpg.qx.module.item.model.useitem;

import com.mmorpg.qx.common.Probability;
import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.common.moduletype.ModuleType;
import com.mmorpg.qx.common.moduletype.SubModuleType;
import com.mmorpg.qx.module.item.manager.GiftManager;
import com.mmorpg.qx.module.item.resource.GiftResource;
import com.mmorpg.qx.module.item.resource.ItemResource;
import com.mmorpg.qx.module.item.service.ItemService;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.reward.model.RewardType;
import com.mmorpg.qx.module.reward.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang peng
 * @description
 * @since 10:51 2021/4/27
 */
@Component
public class UseItemGainGift extends AbstractUseItem {

    @Autowired
    private GiftManager giftManager;


    @Override
    public UseType getUseType() {
        return UseType.USE;
    }

    @Override
    public GainType getGainType() {
        return GainType.GIFT;
    }

    @Override
    public void use(Player player, ItemResource resource, long objectId, int num) {
        // 消耗道具
        ItemService.getInstance().consumeItem(player, objectId, "item", num);

        // 获得礼包
        GiftResource giftResource = giftManager.getResource(resource.getGiftId());
        int type = giftResource.getType();
        List<Integer> itemIds = giftResource.getItemIds();
        List<Integer> nums = giftResource.getNums();
        List<Integer> weights = giftResource.getWeights();
        ModuleInfo moduleInfo = ModuleInfo.valueOf(ModuleType.ITEM, SubModuleType.ITEM_USE);
        if (type == 1) {
            // 获得所有道具
            for (int i = 0; i < itemIds.size(); i++) {
                RewardService.getInstance().grantReward(player, RewardType.ITEM, String.valueOf(itemIds.get(i)),
                        nums.get(i) * num, moduleInfo);
            }
        } else if (type == 2) {
            // 随机一个道具
            List<Probability.RateObject> objects = new ArrayList<>();
            for (int i = 0; i < weights.size(); i++) {
                Probability.RateObject object = new Probability.RateObject(i, weights.get(i));
                objects.add(object);
            }
            int i = Probability.randomObject(objects).getId();
            RewardService.getInstance().grantReward(player, RewardType.ITEM, String.valueOf(itemIds.get(i)),
                    nums.get(i) * num, moduleInfo);
        }
    }

}
