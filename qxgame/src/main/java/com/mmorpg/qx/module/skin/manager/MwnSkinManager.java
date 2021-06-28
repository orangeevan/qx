package com.mmorpg.qx.module.skin.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.module.skin.enums.SkinGetType;
import com.mmorpg.qx.module.skin.enums.SkinQa;
import com.mmorpg.qx.module.skin.resource.MWNSkinResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * @author wang ke
 * @description: 魔物娘皮肤
 * @since 19:12 2020-11-30
 */
@Component
public class MwnSkinManager implements ResourceReload {

    @Static
    private Storage<Integer, MWNSkinResource> mwnSkinResourceStorage;

    @PostConstruct
    public void init(){
        mwnSkinResourceStorage.getAll().forEach(resource -> {
//            resource.setSkinGetType(SkinGetType.valueOf(resource.getGetWay()));
            resource.setSkinQaType(SkinQa.valueOf(resource.getSkinQa()));
        });
    }

    @Override
    public void reload() {
        init();
    }

    @Override
    public Class<?> getResourceClass() {
        return MWNSkinResource.class;
    }

    public Collection<MWNSkinResource> getAllResources(){
        return mwnSkinResourceStorage.getAll();
    }
}
