package com.mmorpg.qx.module.consume.impl;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.consume.AbstractConsume;
import com.mmorpg.qx.module.item.service.ItemService;
import com.mmorpg.qx.module.player.model.Player;

/**
 * 玩家道具消耗
 *
 * @author wang ke
 * @since v1.0 2018年3月26日
 */
public class ItemConsume extends AbstractConsume<Player> {

    /** 道具ID */
    private long objectId;

    @Override
    protected Result doVerify(Player player, int multiple) {
        ItemService itemService = BeanService.getBean(ItemService.class);
        if (!itemService.canConsume(player, objectId, multiple * value)) {
            return Result.valueOf(ManagedErrorCode.NOT_ENOUGH_ITEM);
        }
        return Result.SUCCESS;
    }

    @Override
    public void consume(Player player, ModuleInfo moduleInfo, int multiple) {
        ItemService itemService = BeanService.getBean(ItemService.class);
        itemService.reduceItem(player, objectId, multiple * value);
    }

    @Override
    public AbstractConsume clone() {
        ItemConsume consume = new ItemConsume();
        consume.code = code;
        consume.value = value;
        consume.setType(getType());
        consume.objectId = objectId;
        return consume;
    }

    @Override
    protected boolean canMerge(AbstractConsume consume) {
        boolean clz = consume.getClass().equals(getClass());
        boolean code = consume.getCode().equals(getCode());
        boolean objectId = this.objectId == ((ItemConsume) consume).getObjectId();
        return clz && code && objectId;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }
}
