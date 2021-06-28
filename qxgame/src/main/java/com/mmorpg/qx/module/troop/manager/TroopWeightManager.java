package com.mmorpg.qx.module.troop.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.module.troop.resource.TroopWeightResource;
import org.springframework.stereotype.Component;

/**
 * @author zhang peng
 * @since 11:13 2021/4/24
 */
@Component
public class TroopWeightManager implements ResourceReload {

    @Static
    private Storage<Integer, TroopWeightResource> troopWeightStorage;

    public TroopWeightResource getResource(int prob) {
        return troopWeightStorage.getAll().stream().filter(t -> t.getProb() == prob).findFirst().orElse(null);
    }

    @Override
    public void reload() {

    }

    @Override
    public Class<?> getResourceClass() {
        return TroopWeightResource.class;
    }

    public static TroopWeightManager getInstance() {
        return BeanService.getBean(TroopWeightManager.class);
    }
}
