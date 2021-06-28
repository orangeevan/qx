package com.mmorpg.qx.module.item.service;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.corn.CornTimeTask;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.common.moduletype.ModuleType;
import com.mmorpg.qx.common.moduletype.SubModuleType;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.consume.ConsumeType;
import com.mmorpg.qx.module.consume.impl.ItemConsume;
import com.mmorpg.qx.module.consume.resource.ConsumeResource;
import com.mmorpg.qx.module.item.enums.ItemUpdateReason;
import com.mmorpg.qx.module.item.enums.PackType;
import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.item.model.PackItem;
import com.mmorpg.qx.module.item.model.useitem.AbstractUseItem;
import com.mmorpg.qx.module.item.model.useitem.GainType;
import com.mmorpg.qx.module.item.model.useitem.UseType;
import com.mmorpg.qx.module.item.packet.resp.BackPackUpdateResp;
import com.mmorpg.qx.module.item.resource.ItemResource;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 物品相关业务逻辑实现
 * @since 18:49 2020-08-06
 */
@Component
public class ItemService implements CornTimeTask {

    private Logger logger = SysLoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemManager itemManager;

    Map<UseType, Map<GainType, AbstractUseItem>> typeToUse = new HashMap<>();

    public void register(AbstractUseItem useItem) {
        typeToUse.computeIfAbsent(useItem.getUseType(), k -> new HashMap<>());
        typeToUse.get(useItem.getUseType()).put(useItem.getGainType(), useItem);
    }

    public static ItemService getInstance() {
        return BeanService.getBean(ItemService.class);
    }

    /**
     * 玩家使用道具
     *
     * @param player
     * @param objectId 道具唯一ID
     * @param num      使用数量
     * @param use      使用方式 1出售 2使用
     */
    public void useItem(Player player, long objectId, int num, int use) {
        PackItem packItem = getOrThrowItem(player, objectId);
        ItemResource resource = itemManager.getResource(packItem.getKey());
        if (resource == null) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        UseType useType = UseType.valueOf(use);
        if (useType == null) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        GainType gainType = useType == UseType.SALE ? GainType.NONE : GainType.valueOf(resource.getUseType2());
        if (gainType == null) {
            throw new ManagedException(ManagedErrorCode.SYS_ERROR);
        }
        AbstractUseItem useItem = typeToUse.get(useType).get(gainType);
        useItem.use(player, resource, objectId, num);
    }

    /**
     * 消耗道具
     *
     * @param player
     * @param objectId 道具objectId
     * @param code
     * @param value 消耗数量
     */
    public void consumeItem(Player player, long objectId, String code, int value) {
        ConsumeResource resource = ConsumeResource.valueOf(ConsumeType.Item, code, value);
        ItemConsume consume = resource.getType().create(resource);
        consume.setObjectId(objectId);
        Result result = consume.verify(player, 1);
        if (!result.isSuccess()) {
            throw new ManagedException(result.getCode());
        }
        consume.consume(player, ModuleInfo.valueOf(ModuleType.ITEM, SubModuleType.ITEM_USE), 1);
    }

    /**
     * 消耗道具
     *
     * @param player
     * @param resourceId 道具配置ID
     * @param code
     * @param value 消耗数量
     */
    public void consumeItemByRid(Player player, int resourceId, String code, int value) {
        List<PackItem> items = player.getWarehouse().getItems().values().stream()
                .filter(t -> t.getKey() == resourceId && t.getNum() > 0).collect(Collectors.toList());
        int sumNum = items.stream().mapToInt(PackItem::getNum).sum();
        if (sumNum < value) {
            throw new ManagedException(ManagedErrorCode.NOT_ENOUGH_ITEM);
        }
        for (PackItem item : items) {
            if (item.getNum() < value) {
                consumeItem(player, item.getObjectId(), code, item.getNum());
                value -= item.getNum();
            } else {
                consumeItem(player, item.getObjectId(), code, value);
                break;
            }
        }
    }

    /**
     * 扣除道具
     *
     * @param player
     * @param objectId
     */
    public void reduceItem(Player player, long objectId, int num) {
        PackItem item = player.getWarehouse().getItems().get(objectId);
        int before = item.getNum();
        item.addNum(-num);
        int after = item.getNum();

        // 发送更新消息
        List<PackItem> items = new ArrayList<>();
        items.add(item);
        PacketSendUtility.sendPacket(player, BackPackUpdateResp.valueOf(items, PackType.WAREHOUSE.getType(),
                ItemUpdateReason.USE_ITEM.getReason()));

        // 保存数据
        if (item.getNum() == 0) {
            player.getWarehouse().getItems().remove(objectId);
        }
        itemManager.update(player);

        logger.info("玩家扣除道具, 玩家ID:{}, 道具ID:{}, 操作前数量:{}, 扣除数量{} ,操作后数量:{}",
                player.getObjectId(), objectId, before, num, after);

    }

    /**
     * 判断道具是否可以消耗
     *
     * @param player
     * @param objectId 道具id
     * @param amount   扣除数量
     * @return
     */
    public boolean canConsume(Player player, long objectId, int amount) {
        PackItem item = player.getWarehouse().getItems().get(objectId);
        if (item == null) {
            return false;
        }
        // 叠加上限检查
        ItemResource resource = itemManager.getResource(item.getKey());
        if (amount < 0 || amount > resource.getOverLimit()) {
            throw new ManagedException(ManagedErrorCode.USE_ITEM_NUM_ERROR);
        }
        // 数量检查
        if (amount > item.getNum()) {
            return false;
        }
        // 过期时间检查
        if (resource.getDeadlineTime() != 0 && resource.getDeadlineTime() < System.currentTimeMillis()) {
            clearExpiredItem(player, true);
            throw new ManagedException(ManagedErrorCode.ITEM_OVERDUE);
        }
        return true;
    }

    /**
     * 删除过期道具
     *
     * @param player
     * @param send
     */
    public void clearExpiredItem(Player player, boolean send) {
        Map<Long, PackItem> items = player.getWarehouse().getItems();
        List<PackItem> updates = new ArrayList<>();
        Collection<PackItem> packItems = items.values();
        if (!CollectionUtils.isEmpty(packItems)) {
            Iterator<PackItem> iterator = packItems.iterator();
            while (iterator.hasNext()) {
                PackItem item = iterator.next();
                ItemResource resource = itemManager.getResource(item.getKey());
                if (resource.getDeadlineTime() != 0 && resource.getDeadlineTime() < System.currentTimeMillis()) {
                    iterator.remove();
                    item.setNum(0);
                    updates.add(item);
                }
            }
            itemManager.update(player);
            // TODO 发送邮件给前端
            if (send) {
                // 发送更新消息
                PacketSendUtility.sendPacket(player, BackPackUpdateResp.valueOf(updates, PackType.WAREHOUSE.getType(),
                        ItemUpdateReason.ITEM_EXPIRED.getReason()));
            }
            for (PackItem update : updates) {
                logger.info("玩家道具过期删除, 玩家ID:{}, 道具ID:{}", player.getObjectId(), update.getObjectId());
            }
        }
    }

    public PackItem getOrThrowItem(Player player, long itemId) {
        PackItem item = player.getWarehouse().getItems().get(itemId);
        if (item == null) {
            throw new ManagedException(ManagedErrorCode.ITEM_NOT_EXIST);
        }
        return item;
    }

    @Override
    public void everyDayZero() {
        try {
            List<Player> players = PlayerManager.getInstance().getAllOnlinePlayer();
            for (Player player : players) {
                clearExpiredItem(player, true);
            }
        } catch (Exception e) {
            logger.error("删除在线玩家过期道具异常");
        }
    }

}
