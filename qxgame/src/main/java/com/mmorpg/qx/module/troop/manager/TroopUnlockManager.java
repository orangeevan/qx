package com.mmorpg.qx.module.troop.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.module.troop.resource.TroopUnlockResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhang peng
 * @since 16:51 2021/4/20
 */
@Component
public class TroopUnlockManager implements ResourceReload {

    @Static
    private Storage<Integer, TroopUnlockResource> troopUnlockStorage;

    public TroopUnlockResource getResource(int type, int index) {
        return troopUnlockStorage.getAll().stream().filter(t -> t.getType() == type && t.getIndex() == index).findFirst()
                .orElse(null);
    }

    public List<TroopUnlockResource> getUnlockResources() {
        return troopUnlockStorage.getAll().stream().filter(t -> t.getMoneyId() == 0).collect(Collectors.toList());
    }

    @Override
    public void reload() {

    }

    @Override
    public Class<?> getResourceClass() {
        return TroopUnlockResource.class;
    }
}
