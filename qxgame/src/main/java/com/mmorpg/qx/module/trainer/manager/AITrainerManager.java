package com.mmorpg.qx.module.trainer.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.module.trainer.resource.AITrainerResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author wang ke
 * @description: Ai驯养师配置
 * @since 19:27 2020-11-30
 */
@Component
public class AITrainerManager implements ResourceReload {

    /**
     * ai驯养师配置
     */
    @Static
    private Storage<Integer, AITrainerResource> aiTrainerResource;

    @Override
    public void reload() {
    }

    @Override
    public Class<?> getResourceClass() {
        return AITrainerResource.class;
    }

    public void init() {
        Collection<AITrainerResource> allAITrainers = aiTrainerResource.getAll();
        if (!CollectionUtils.isEmpty(allAITrainers)) {
            allAITrainers.stream().forEach(AITrainerResource::init);
        }
    }

    public Collection<AITrainerResource> getAllResources() {
        return aiTrainerResource.getAll();
    }

    public AITrainerResource getAITrainerResource(int id) {
        return aiTrainerResource.get(id, true);
    }
}
