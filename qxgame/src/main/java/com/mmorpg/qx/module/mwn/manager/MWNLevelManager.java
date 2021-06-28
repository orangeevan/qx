package com.mmorpg.qx.module.mwn.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.module.mwn.resource.MWNLevelResource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;

/**
 * @author zhang peng
 * @description: 魔物娘等级manager
 * @since 20:01 2021/3/11
 */
@Component
public class MWNLevelManager implements ResourceReload {

    @Static
    private Storage<Integer, MWNLevelResource> levelResourceStorage;

    /**
     * @param id 魔物娘等级
     * @return
     */
    public MWNLevelResource getLevelResource(int id) {
        return levelResourceStorage.get(id, true);

    }

    /**
     * 魔物娘最大等级
     */
    public int getMaxLevel() {
        return getAllResource().stream().max(Comparator.comparingInt(MWNLevelResource::getId))
                .map(MWNLevelResource::getId).orElse(-1);
    }


    private Collection<MWNLevelResource> getAllResource() {
        return levelResourceStorage.getAll();
    }

    @Override
    public void reload() {

    }

    @Override
    public Class<?> getResourceClass() {
        return MWNLevelResource.class;
    }
}
