package com.mmorpg.qx.module.mwn.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.module.mwn.resource.MWNStarResource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;

/**
 * @author zhang peng
 * @description: 魔物娘潜能星数manager
 * @since 20:11 2021/3/16
 */
@Component
public class MWNStarManager implements ResourceReload {

    @Static
    private Storage<Integer, MWNStarResource> starResourceStorage;

    /**
     * @param mwnKey 魔物娘配置ID
     * @param starLv 魔物娘星数
     * @return
     */
    public MWNStarResource getStarResource(int mwnKey, int starLv) {
        return getAllResource().stream().filter(t -> t.getMwnKey() == mwnKey && t.getStarLv() == starLv)
                .findFirst().orElse(null);
    }

    /**
     * 魔物娘最大星数
     */
    public int getMaxStarLv(int mwnKey) {
        return getAllResource().stream().filter(t -> t.getMwnKey() == mwnKey)
                .max(Comparator.comparingInt(MWNStarResource::getStarLv)).map(MWNStarResource::getStarLv).orElse(0);
    }

    private Collection<MWNStarResource> getAllResource() {
        return starResourceStorage.getAll();
    }

    @Override
    public void reload() {

    }

    @Override
    public Class<?> getResourceClass() {
        return MWNStarResource.class;
    }
}
