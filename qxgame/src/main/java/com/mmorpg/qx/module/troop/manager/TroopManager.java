package com.mmorpg.qx.module.troop.manager;

import com.alibaba.fastjson.JSON;
import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.utility.JsonUtils;
import com.haipaite.common.utility.StringUtils;
import com.mmorpg.qx.common.rule.EntityOfPlayerUpdateRule;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateType;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.troop.entity.TroopEntity;
import com.mmorpg.qx.module.troop.model.Troop;
import com.mmorpg.qx.module.troop.model.TroopStorage;
import com.mmorpg.qx.module.troop.service.TroopService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhang peng
 * @since 15:55 2021/5/11
 */
@Component
public class TroopManager implements EntityOfPlayerUpdateRule {

    @Inject
    private EntityCacheService<Long, TroopEntity> troopCacheService;

    @Override
    public void initPlayer(Player player) {
        TroopEntity entity = troopCacheService.loadOrCreate(player.getPlayerEnt().getPlayerId(),
                id -> {
                    TroopEntity en = new TroopEntity();
                    en.setPlayerId(id);
                    return en;
                });
        player.setTroopEntity(entity);

        // 初始化编队
        if (StringUtils.isBlank(entity.getTroopStorage())) {
            TroopStorage troopStorage = new TroopStorage();
            List<Troop> troops = TroopService.getInstance().getUnlockTroops();
            troopStorage.setTroops(troops);
            troopStorage.setPlayer(player);
            player.setTroopStorage(troopStorage);

            // 设置编队默认卡组
            TroopService.getInstance().setDefaultCards(player);
        } else {
            TroopStorage troopStorage = JSON.parseObject(entity.getTroopStorage(), TroopStorage.class);
            troopStorage.setPlayer(player);
            player.setTroopStorage(troopStorage);
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
                        return "updateTroopEntity";
                    }

                    @Override
                    public void doRun() {
                        logoutWriteBack(player);
                    }
                }, 1);
    }

    @Override
    public void logoutWriteBack(Player player) {
        player.getTroopEntity().setTroopStorage(JsonUtils.object2String(player.getTroopStorage()));
        troopCacheService.writeBack(player.getPlayerEnt().getPlayerId(), player.getTroopEntity());
    }
}
