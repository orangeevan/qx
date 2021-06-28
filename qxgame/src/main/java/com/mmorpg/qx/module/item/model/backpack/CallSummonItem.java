package com.mmorpg.qx.module.item.model.backpack;

import com.mmorpg.qx.module.item.enums.ItemType;
import com.mmorpg.qx.module.item.model.ItemStorage;
import com.mmorpg.qx.module.item.model.PackItem;
import com.mmorpg.qx.module.player.model.Player;
import org.springframework.stereotype.Component;

/**
 * @author wang ke
 * @description: 召唤卡
 * @since 15:26 2020-08-07
 */
@Component
public class CallSummonItem extends AbstractBackpackItemUse {

    public CallSummonItem() {
        super(ItemType.MWN);
    }

    @Override
    int verifyUse(Player player, ItemStorage backpack, int itemId, int num) {
        //验证格子是否存在等
        return super.verifyUse(player, backpack, itemId, num);
    }

    @Override
    public PackItem[] doUse(Player player, ItemStorage backpack, int itemId, int num) {
        return null;
        //召唤卡在场景中，通过使用卡包卡牌实现
        //CardBagStorage cardBagStorage = (CardBagStorage) backpack;
        //先扣除卡牌数量
        //List<PackItem> packItems = cardBagStorage.removeItemByFilter(entity -> entity.getResource().getId() == itemId, num);
        //获取召唤物出生位置
//        int playerGridId = player.getPosition().getGridId();
//        MapGrid mapGrid = MapResourceManager.getInstance().getWorldMap(player.getMapId()).getMapGrid(playerGridId);
//        //召唤物出生位置在玩家所站格子绑定格子上
//        WorldPosition summonPosition = WorldMapService.getInstance().createWorldPosition(player.getWorldMapInstance(), mapGrid.getBind());
//        summonPosition.setIsSpawned(true);
//        ItemResource itemResource = ItemManager.getInstance().getResource(itemId);
//        int objectId = Integer.valueOf(itemResource.getExt());
//        ObjectResource objectResource = ObjectManager.getInstance().getObjectResource(objectId);
//        AbstractObjectCreater creater = SummonCreater.getCreater(ObjectType.SUMMON);
//        Map<String, Object> parms = new HashMap<>();
//        parms.put("Player", player);
//        SpawnGroupResource spawnsource = SpawnManager.getInstance().getSpawn(objectResource.getKey());
//        Summon summon = (Summon) creater.create(spawnsource, objectResource,	player.getWorldMapInstance(), parms);
//        summon.setAi(AIType.SUMMON.create());
//        player.getSummons().put(summon.getObjectId().intValue(), summon);
//        World.getInstance().spawn(summon);
//        //进行地图广播
//        MonsterInfoResp monsterInfoResp = new MonsterInfoResp();
//        monsterInfoResp.setObjectId(summon.getObjectId());
//        monsterInfoResp.setModelId(objectId);
//        monsterInfoResp.setGridId(summonPosition.getGridId());
//        monsterInfoResp.setDir(summon.getDir().getDir());
//        //怪物出生了
//        WorldMapService.getInstance().broadcastInWorldMap(player.getWorldMapInstance(), monsterInfoResp);
       // return (PackItem[]) packItems.toArray();
    }
}
