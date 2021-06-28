package com.mmorpg.qx.module.item.model.useitem;

import com.mmorpg.qx.module.item.resource.ItemResource;
import com.mmorpg.qx.module.item.service.ItemService;
import com.mmorpg.qx.module.player.model.Player;


/**
 * @author zhang peng
 * @description
 * @since 9:56 2021/4/27
 */
public abstract class AbstractUseItem {

    protected AbstractUseItem() {
        ItemService.getInstance().register(this);
    }

    public abstract UseType getUseType();

    public abstract GainType getGainType();

    public abstract void use(Player player, ItemResource resource, long objectId, int num);
}
