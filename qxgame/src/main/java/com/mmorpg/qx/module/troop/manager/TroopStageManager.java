package com.mmorpg.qx.module.troop.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.module.troop.resource.TroopStageResource;
import org.springframework.stereotype.Component;

/**
 * @author zhang peng
 * @since 11:44 2021/4/24
 */
@Component
public class TroopStageManager implements ResourceReload {

    @Static
    private Storage<Integer, TroopStageResource> troopStageStorage;

    public TroopStageResource getResource(int stage) {
        return troopStageStorage.getAll().stream().filter(t -> t.getRoundStage() == stage).findFirst().orElse(null);
    }

    @Override
    public void reload() {

    }

    @Override
    public Class<?> getResourceClass() {
        return TroopStageResource.class;
    }
}
