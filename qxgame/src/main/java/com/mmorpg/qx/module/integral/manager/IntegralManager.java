package com.mmorpg.qx.module.integral.manager;

import com.alibaba.fastjson.JSON;
import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.utility.StringUtils;
import com.mmorpg.qx.common.rule.EntityOfPlayerUpdateRule;
import com.mmorpg.qx.module.integral.entity.IntegralEntity;
import com.mmorpg.qx.module.integral.model.IntegralStore;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateType;
import com.mmorpg.qx.module.player.model.Player;
import org.springframework.stereotype.Component;

/**
 * @author zhang peng
 * @description:
 * @since 20:05 2021/3/4
 */
@Component
public class IntegralManager implements EntityOfPlayerUpdateRule {

    @Inject
    private EntityCacheService<Long, IntegralEntity> cacheService;


    @Override
    public void initPlayer(Player player) {
        IntegralEntity integralEntity = cacheService.loadOrCreate(player.getObjectId(), id -> {
            IntegralEntity en = new IntegralEntity();
            en.setPlayerId(id);
            return en;
        });

        player.setIntegralEntity(integralEntity);

        if (StringUtils.isBlank(integralEntity.getIntegralStore())) {
            IntegralStore integralStore = new IntegralStore();
            player.setIntegralStore(integralStore);
        } else {
            IntegralStore integralStore = JSON.parseObject(integralEntity.getIntegralStore(), IntegralStore.class);
            player.setIntegralStore(integralStore);
        }
    }

    @Override
    public void update(Player player) {
        player.getUpdateTaskManager().addUpdateTaskDelayMinute(CreatureUpdateType.PLAYER_INTEGRAL,
                new AbstractDispatcherHashCodeRunable() {
                    @Override
                    public int getDispatcherHashCode() {
                        return player.getDispatcherHashCode();
                    }

                    @Override
                    public String name() {
                        return "UPDATE_PLAYER_INTEGRAL";
                    }

                    @Override
                    public void doRun() {
                        logoutWriteBack(player);
                    }
                }, 1);
    }

    @Override
    public void logoutWriteBack(Player player) {
        cacheService.writeBack(player.getObjectId(), player.getIntegralEntity());
    }

}
