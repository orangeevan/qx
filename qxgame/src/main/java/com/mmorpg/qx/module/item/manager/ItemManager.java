package com.mmorpg.qx.module.item.manager;

import com.alibaba.fastjson.JSON;
import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.utility.DateUtils;
import com.haipaite.common.utility.JsonUtils;
import com.haipaite.common.utility.StringUtils;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.configValue.ConfigValueManager;
import com.mmorpg.qx.common.rule.EntityOfPlayerUpdateRule;
import com.mmorpg.qx.module.item.entity.PackEntity;
import com.mmorpg.qx.module.item.model.EquipmentStorage;
import com.mmorpg.qx.module.item.model.ItemStorage;
import com.mmorpg.qx.module.item.model.PackItem;
import com.mmorpg.qx.module.item.resource.ItemResource;
import com.mmorpg.qx.module.item.service.ItemService;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateType;
import com.mmorpg.qx.module.player.model.Player;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public final class ItemManager implements ResourceReload, EntityOfPlayerUpdateRule {

    @Static
    private Storage<Integer, ItemResource> itemResourceStorage;

    @Inject
    private EntityCacheService<Long, PackEntity> packCacheService;

    @PostConstruct
    protected void init() {
        initResource();
    }

    public static ItemManager getInstance() {
        return BeanService.getBean(ItemManager.class);
    }

    @Override
    public void initPlayer(Player player) {
        PackEntity packEntity = packCacheService.loadOrCreate(player.getPlayerEnt().getPlayerId(),
                id -> {
                    PackEntity pe = new PackEntity();
                    pe.setPlayerId(id);
                    return pe;
                });
        player.setPackEntity(packEntity);

        // 初始化仓库
        if (StringUtils.isBlank(packEntity.getWarehouse())) {
            ItemStorage warehouse = new ItemStorage();
            player.setWarehouse(warehouse);
            warehouse.setOwner(player);
            warehouse.setItems(createItems());
        } else {
            ItemStorage warehouse = JSON.parseObject(packEntity.getWarehouse(), ItemStorage.class);
            player.setWarehouse(warehouse);
            warehouse.setOwner(player);
        }
        // 删除仓库过期道具
        ItemService.getInstance().clearExpiredItem(player, false);

        // 初始化装备栏
        if (StringUtils.isBlank(packEntity.getEquipmentStorage())) {
            EquipmentStorage equipmentStorage = new EquipmentStorage();
            player.setEquipmentStorage(equipmentStorage);
            equipmentStorage.setOwner(player);
        } else {
            EquipmentStorage equipmentStorage = JSON.parseObject(packEntity.getEquipmentStorage(), EquipmentStorage.class);
            player.setEquipmentStorage(equipmentStorage);
            equipmentStorage.setOwner(player);
        }
    }

    @Override
    public void update(Player player) {
        player.getUpdateTaskManager().addUpdateTaskDelayMinute(CreatureUpdateType.ITEM_ENTITY_UPDATE,
                new AbstractDispatcherHashCodeRunable() {
                    @Override
                    public int getDispatcherHashCode() {
                        return player.getDispatcherHashCode();
                    }

                    @Override
                    public String name() {
                        return "updatePackEntity";
                    }

                    @Override
                    public void doRun() {
                        logoutWriteBack(player);
                    }
                }, 1);
    }

    @Override
    public void logoutWriteBack(Player player) {
        player.getPackEntity().setWarehouse(JsonUtils.object2String(player.getWarehouse()));
        player.getPackEntity().setEquipmentStorage(JsonUtils.object2String(player.getEquipmentStorage()));
        player.getPackEntity().setTroopStorage(JsonUtils.object2String(player.getTroopStorage()));
        packCacheService.writeBack(player.getPlayerEnt().getPlayerId(), player.getPackEntity());
    }

    public ItemResource getResource(int resourceId) {
        return itemResourceStorage.get(resourceId, true);
    }

    public Collection<ItemResource> getAllItemResource() {
        return itemResourceStorage.getAll();
    }

    public PackEntity load(long playerId) {
        return packCacheService.load(playerId);
    }

    /***
     * 卡包组最大卡牌数,默认30
     * @return
     */
    public int getCardBagsMaxCardNum() {
        int cardBagsMaxCardNum = ConfigValueManager.getInstance().getIntConfigValue("CardBagsMaxCardNum");
        return cardBagsMaxCardNum == 0 ? 30 : cardBagsMaxCardNum;
    }

    /**
     * 战斗中手牌最大数量，默认7
     *
     * @return
     */
    public int getRoundMaxCardNum() {
        int roundMaxCardNum = ConfigValueManager.getInstance().getIntConfigValue("RoundMaxCardNum");
        return roundMaxCardNum == 0 ? 7 : roundMaxCardNum;
    }

    // 创建物品 测试用
    private Map<Long, PackItem> createItems() {
        Map<Long, PackItem> map = new HashMap<>();
        Collection<ItemResource> itemResources = getAllItemResource();
        for (ItemResource resource : itemResources) {
            if (resource.getId() == 3035005) {
                continue;
            }
            PackItem packItem = PackItem.valueOf(resource.getId(), 999);
            map.put(packItem.getObjectId(), packItem);
        }
        return map;
    }

    @Override
    public void reload() {
        initResource();
    }

    private void initResource() {
        Collection<ItemResource> resources = itemResourceStorage.getAll();
        if (!CollectionUtils.isEmpty(resources)) {
            for (ItemResource resource : resources) {
                if (!StringUtils.isBlank(resource.getDeadline())) {
                    resource.setDeadlineTime(DateUtils.string2Date(resource.getDeadline(),
                            "yyyy-MM-dd HH:mm:ss").getTime());
                }
            }
        }
    }

    @Override
    public Class<?> getResourceClass() {
        return ItemResource.class;
    }

}
