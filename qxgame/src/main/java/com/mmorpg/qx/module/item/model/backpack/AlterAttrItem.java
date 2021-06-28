package com.mmorpg.qx.module.item.model.backpack;

import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.item.model.ItemStorage;
import com.mmorpg.qx.module.item.model.PackItem;
import com.mmorpg.qx.module.item.resource.ItemResource;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.player.model.Player;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang ke
 * @description: 改变玩家属性类道具
 * @since 17:44 2020-08-07
 */
@Component
public class AlterAttrItem extends AbstractBackpackItemUse {

    @Override
    PackItem[] doUse(Player player, ItemStorage backpack, int itemId, int num) {
        //先扣除物品
//        List<PackItem> packItems = backpack.removeItemByFilter(entity -> entity.getResource().getId() == itemId, num);
//        ItemResource resource = ItemManager.getInstance().getResource(itemId);
//        List<Attr> attrs = resource.getAttrs();
//        List<Attr>  newAttrs = new ArrayList<>();
//        attrs.stream().forEach(attr -> newAttrs.add(new Attr(attr.getType(), attr.getValue() * num)));
//        //player.getGameAttrs().addModifiers(AttrEffectId.valueOf(AttrEffectType.ITEM_EFFECT), newAttrs);
//        return (PackItem[])packItems.toArray();
        return null;
    }
}
