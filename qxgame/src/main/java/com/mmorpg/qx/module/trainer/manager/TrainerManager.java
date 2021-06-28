package com.mmorpg.qx.module.trainer.manager;

import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.utility.JsonUtils;
import com.haipaite.common.utility.StringUtils;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.rule.EntityOfPlayerUpdateRule;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateType;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.trainer.entity.PlayerTrainerEntity;
import com.mmorpg.qx.module.trainer.module.PlayerTrainer;
import com.mmorpg.qx.module.trainer.resource.AITrainerResource;
import com.mmorpg.qx.module.trainer.resource.PlayerTrainerResource;
import com.mmorpg.qx.module.trainer.service.PlayerTrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

/**
 * @author wang ke
 * @description: 驯养师manager
 * @since 15:08 2020-08-13
 */
@Component
public class TrainerManager implements EntityOfPlayerUpdateRule {

    @Autowired
    private AITrainerManager aiTrainerManager;

    @Autowired
    private PlayerTrainerManager playerTrainerManager;

    @Inject
    private EntityCacheService<Long, PlayerTrainerEntity> playerTrainerEntityCache;

    @PostConstruct
    private void init() {
        aiTrainerManager.init();
        playerTrainerManager.init();
    }

    @Override
    public void initPlayer(Player player) {
        PlayerTrainerEntity entity = playerTrainerEntityCache.loadOrCreate(player.getPlayerEnt().getPlayerId(), id -> {
            PlayerTrainerEntity pe = new PlayerTrainerEntity();
            pe.setPlayerId(id);
            return pe;
        });
        player.setTrainerEntity(entity);
        //初始化
        if (!StringUtils.isBlank(entity.getTrainerJson())) {
            List<PlayerTrainer> playerTrainer = JsonUtils.string2List(entity.getTrainerJson(), PlayerTrainer.class);
            if (!CollectionUtils.isEmpty(playerTrainer)) {
                playerTrainer.forEach(trainer -> trainer.setOwner(player));
                player.setPlayerTrainers(playerTrainer);
            }
        } else {
            Collection<PlayerTrainerResource> resources = playerTrainerManager.getAllResources();
            if (!CollectionUtils.isEmpty(resources)) {
                resources.stream().filter(t -> t.getChipKey() == 0).forEach(t -> PlayerTrainerService.getInstance()
                        .createTrainer(player, t.getId()));
            }
        }
    }


    @Override
    public void update(Player player) {
        player.getUpdateTaskManager().addUpdateTaskDelayMinute(CreatureUpdateType.PLAYER_TRAINER, new AbstractDispatcherHashCodeRunable() {
            @Override
            public int getDispatcherHashCode() {
                return player.getDispatcherHashCode();
            }

            @Override
            public String name() {
                return "UpdatePlayerTrainer";
            }

            @Override
            public void doRun() {
                logoutWriteBack(player);
            }
        }, 1);
    }

    @Override
    public void logoutWriteBack(Player player) {
        player.getTrainerEntity().setTrainerJson(JsonUtils.object2String(player.getPlayerTrainers()));
        playerTrainerEntityCache.writeBack(player.getPlayerEnt().getPlayerId(), player.getTrainerEntity());
    }

    /***
     * 驯养师id获得配置数据
     * @param id
     * @return
     */
    public PlayerTrainerResource getPlayerTrainerResource(int id) {
        return playerTrainerManager.getPlayerTrainerResource(id);
    }

    public AITrainerResource getAITrainerResource(int id) {
        return aiTrainerManager.getAITrainerResource(id);
    }

    public static TrainerManager getInstance() {
        return BeanService.getBean(TrainerManager.class);
    }
}
