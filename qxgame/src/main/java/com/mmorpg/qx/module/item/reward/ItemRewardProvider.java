package com.mmorpg.qx.module.item.reward;

import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.item.enums.ItemUpdateReason;
import com.mmorpg.qx.module.item.enums.PackType;
import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.item.model.PackItem;
import com.mmorpg.qx.module.item.packet.resp.BackPackUpdateResp;
import com.mmorpg.qx.module.item.resource.ItemResource;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.reward.model.RewardItem;
import com.mmorpg.qx.module.reward.model.RewardProvider;
import com.mmorpg.qx.module.reward.model.RewardType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ItemRewardProvider extends RewardProvider {

	@Autowired
	private ItemManager itemManager;

	@Override
	public RewardType getType() {
		return RewardType.ITEM;
	}

	/** 仓库容量限制 */
	private static final int WAREHOUSE_CAPACITY = 10000;

	@Override
	public void withdraw(Player player, RewardItem rewardItem, ModuleInfo module) {
		List<PackItem> updates = new ArrayList<>();
		Map<Long, PackItem> items = player.getWarehouse().getItems();

		int amount = rewardItem.getAmount();
		int resourceId = Integer.parseInt(rewardItem.getCode());
		ItemResource resource = itemManager.getResource(resourceId);
		int overLimit = resource.getOverLimit();

		if (overLimit == 1) { // 不可叠加
			for (int i = 0; i < amount; i++) {
				// TODO valueOf()修改
				PackItem item = PackItem.valueOf(resourceId, 1);
				putItem(items, updates, item);
			}
		} else {
			Optional<PackItem> any = items.values().stream().filter(t -> t.getKey() == resourceId
					&& t.getNum() < overLimit).findAny();
			if (any.isPresent()) {
				PackItem item = any.get();
				if (overLimit - item.getNum() - amount >= 0) { // 当前格子可以装下
					item.addNum(amount);
					putItem(items, updates, item);
				} else {
					// 先装满当前格子
					item.addNum(overLimit - item.getNum());
					putItem(items, updates, item);
					// 再装剩下的
					batchAdd(items, updates, amount - (overLimit - item.getNum()), overLimit, resourceId);
				}
			} else {
				batchAdd(items, updates, amount, overLimit, resourceId);
			}
		}

		// 保存数据
		itemManager.update(player);

		// 发送更新消息
		PacketSendUtility.sendPacket(player, BackPackUpdateResp.valueOf(updates, PackType.WAREHOUSE.getType(),
				ItemUpdateReason.GAIN_ITEM.getReason()));
	}

	private void batchAdd(Map<Long, PackItem> items, List<PackItem> updates, int total, int overLimit, int resourceId) {
		if (total <= overLimit) {
			PackItem item = PackItem.valueOf(resourceId, total);
			putItem(items, updates, item);
		} else {
			int times = total / overLimit;
			for (int i = 0; i < times; i++) {
				PackItem item = PackItem.valueOf(resourceId, overLimit);
				putItem(items, updates, item);
			}
			PackItem item = PackItem.valueOf(resourceId, total - overLimit * times);
			putItem(items, updates, item);
		}
	}

	private void putItem(Map<Long, PackItem> items, List<PackItem> updates, PackItem item) {
		items.put(item.getObjectId(), item);
		updates.add(item);
	}
}
