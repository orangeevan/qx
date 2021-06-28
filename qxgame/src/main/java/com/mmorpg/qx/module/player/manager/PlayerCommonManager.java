package com.mmorpg.qx.module.player.manager;

import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.mmorpg.qx.common.rule.EntityOfPlayerUpdateRule;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateType;
import com.mmorpg.qx.module.player.entity.PlayerCommonEnt;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.player.packet.SyncCommonDataResp;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;

/**
 * @author wang ke
 * @description: 玩家通用数据管理
 * @since 15:36 2020-10-19
 */
@Component
public class PlayerCommonManager implements EntityOfPlayerUpdateRule {

    @Inject
    private EntityCacheService<Long, PlayerCommonEnt> commonDataCache;

    private static PlayerCommonManager instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    @Override
    public void initPlayer(Player player) {
        PlayerCommonEnt commonEnt = commonDataCache.loadOrCreate(player.getPlayerEnt().getPlayerId(), id -> {
            PlayerCommonEnt playerCommonEnt = new PlayerCommonEnt();
            playerCommonEnt.setPlayerId(id);
            return playerCommonEnt;
        });
        commonEnt.unSerialize();
        if (!CollectionUtils.isEmpty(commonEnt.getCommonData())) {
            SyncCommonDataResp resp = SyncCommonDataResp.valueOf(commonEnt.getCommonData());
            PacketSendUtility.sendPacket(player, resp);
        }
        player.setCommonEnt(commonEnt);
    }

    @Override
    public void update(Player player) {
        player.getUpdateTaskManager().addUpdateTaskDelayMinute(CreatureUpdateType.PLAYER_COMMON, new AbstractDispatcherHashCodeRunable() {
            @Override
            public int getDispatcherHashCode() {
                return player.getDispatcherHashCode();
            }

            @Override
            public String name() {
                return "Update player common data";
            }

            @Override
            public void doRun() {
                logoutWriteBack(player);
            }
        }, 1);
    }

    @Override
    public void logoutWriteBack(Player player) {
        if (player.getCommonEnt().getChange().compareAndSet(true, false)) {
            commonDataCache.writeBack(player.getPlayerEnt().getPlayerId(), player.getCommonEnt());
        }
    }

    public static PlayerCommonManager getInstance() {
        return instance;
    }
}
