package com.mmorpg.qx.module.mwn.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.module.mwn.resource.MWNCasteResource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;

/**
 * @author zhang peng
 * @description: 魔物娘阶数manager
 * @since 17:17 2021/3/13
 */
@Component
public class MWNCasteManager implements ResourceReload {

    @Static
    private Storage<Integer, MWNCasteResource> casteResourceStorage;

    /**
     * @param mwnKey 魔物娘配置ID
     * @param casteLv 魔物娘阶数
     * @return
     */
    public MWNCasteResource getCasteResource(int mwnKey, int casteLv) {
        return getAllResource().stream().filter(t -> t.getMwnKey() == mwnKey && t.getCasteLv() == casteLv)
                .findFirst().orElse(null);
    }

    /**
     *  魔物娘最大阶数 不同魔物娘最大阶数不同
     */
    public int getMaxCasteLv(int mwnKey) {
        return getAllResource().stream().filter(t -> t.getMwnKey() == mwnKey)
                .max(Comparator.comparingInt(MWNCasteResource::getCasteLv)).map(MWNCasteResource::getCasteLv).orElse(0);
    }

    private Collection<MWNCasteResource> getAllResource() {
        return casteResourceStorage.getAll();
    }

    @Override
    public void reload() {

    }

    @Override
    public Class<?> getResourceClass() {
        return MWNCasteResource.class;
    }
}
