package com.mmorpg.qx.module.trainer.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.module.trainer.resource.PlayerTrainerResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author wang ke
 * @description: 玩家驯养师管理
 * @since 19:26 2020-11-30
 */
@Component
public class PlayerTrainerManager implements ResourceReload {
    /**
     * 玩家驯养师驯养师配置
     */
    @Static
    private Storage<Integer, PlayerTrainerResource> playerTrainerResources;

    @Override
    public void reload() {
        init();
    }

    @Override
    public Class<?> getResourceClass() {
        return PlayerTrainerResource.class;
    }

    public void init(){
        Collection<PlayerTrainerResource> allPlayerTrainers = playerTrainerResources.getAll();
        if (!CollectionUtils.isEmpty(allPlayerTrainers)) {
            allPlayerTrainers.forEach(PlayerTrainerResource::init);
        }
    }

    public Collection<PlayerTrainerResource> getAllResources(){
        return playerTrainerResources.getAll();
    }

    /***
     * 驯养师id获得配置数据
     * @param id
     * @return
     */
    public PlayerTrainerResource getPlayerTrainerResource(int id) {
        return playerTrainerResources.get(id, true);
    }
}
