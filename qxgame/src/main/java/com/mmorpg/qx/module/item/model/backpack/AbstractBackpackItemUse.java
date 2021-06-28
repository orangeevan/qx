package com.mmorpg.qx.module.item.model.backpack;

import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.item.enums.ItemType;
import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.item.model.ItemStorage;
import com.mmorpg.qx.module.item.model.PackItem;
import com.mmorpg.qx.module.player.model.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wang ke
 * @description: 背包物品使用
 * @since 13:58 2020-08-07
 */
public abstract class AbstractBackpackItemUse {

    private static Map<ItemType, AbstractBackpackItemUse> types2Use = new HashMap<>();

    public ItemType type;

    public AbstractBackpackItemUse(ItemType type) {
        this.type = type;
        types2Use.put(type, this);
    }

    public AbstractBackpackItemUse() {

    }

    /***
     * 验证能否使用，比如物品使用次数限制，CD等
     * @param player
     * @param backpack
     * @param itemId
     * @param num
     * @return
     */
    int verifyUse(Player player, ItemStorage backpack, int itemId, int num) {
//        //对物品数量验证
//        if (backpack.getItemSizeByFilter(entity -> entity.getResource().getId() == itemId) < num) {
//            return ManagedErrorCode.ITEMS_NUM_LACK;
//        }
        //TODO  后面根据具体业务再添加
        return ManagedErrorCode.OKAY;
    }


    /***
     * 物品使用逻辑，包括物品数量扣除，产生效果等,返回变化的物品
     * @param player
     * @param backpack
     * @param itemId
     * @param num
     */
    abstract PackItem[] doUse(Player player, ItemStorage backpack, int itemId, int num);


    /***
     * 物品使用完后，需要同步客户端，背包数据反序列写回数据库等
     * @param player
     * @param backpack
     * @param packItems
     */
    void afterUse(Player player, ItemStorage backpack, PackItem[] packItems, boolean needSyncClient) {
        /** 写回数据库 */
        ItemManager.getInstance().update(player);
        //是否需要同步客户端
        if (needSyncClient) {
//            BackPackUpdateResp packUpdateResp = BackPackUpdateResp.valueOf(packItems, PackType.BACKPACK.getType());
//            PacketSendUtility.sendPacket(player, packUpdateResp);
        }
    }

    /***
     * 对外物品使用
     * @param player
     * @param backpack 可能是背包或者卡包
     * @param itemId
     * @param num
     */
    public void useItem(Player player, ItemStorage backpack, int itemId, int num, boolean needSyncClient) {
        int errorCode = verifyUse(player, backpack, itemId, num);
        if (errorCode != ManagedErrorCode.OKAY) {
            throw new ManagedException(errorCode);
        }
        PackItem[] packItems = doUse(player, backpack, itemId, num);
        afterUse(player, backpack, packItems, needSyncClient);
    }

    public static AbstractBackpackItemUse getByItemType(ItemType type) {
        return types2Use.get(type);
    }
}
