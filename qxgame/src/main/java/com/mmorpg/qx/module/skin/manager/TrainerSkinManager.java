package com.mmorpg.qx.module.skin.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.module.skin.enums.SkinQa;
import com.mmorpg.qx.module.skin.resource.PlayerTrainerSkinResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * @author wang ke
 * @description:
 * @since 19:12 2020-11-30
 */
@Component
public class TrainerSkinManager implements ResourceReload {
    @Static
    private Storage<Integer, PlayerTrainerSkinResource> trainerSkinResourceStorage;

    @PostConstruct
    public void init(){
        trainerSkinResourceStorage.getAll().forEach(resource -> resource.setSkinQaType(SkinQa.valueOf(resource.getSkinQa())));
    }

    @Override
    public void reload() {
        init();
    }

    @Override
    public Class<?> getResourceClass() {
        return PlayerTrainerSkinResource.class;
    }

    public Collection<PlayerTrainerSkinResource> getAllResouces(){
        return trainerSkinResourceStorage.getAll();
    }
}
