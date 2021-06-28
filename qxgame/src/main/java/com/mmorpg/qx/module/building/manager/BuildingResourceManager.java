package com.mmorpg.qx.module.building.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.module.building.resource.BuildingResource;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * @author wang ke
 * @description: 建筑资源管理
 * @since 14:59 2020-09-28
 */
@Component
public class BuildingResourceManager implements ResourceReload {

    @Static
    private Storage<Integer, BuildingResource> buildingStorage;

    public Collection<BuildingResource> getBuildingResources() {
        return buildingStorage.getAll();
    }

    public static BuildingResourceManager getInstance() {
        return BeanService.getBean(BuildingResourceManager.class);
    }

    public BuildingResource getResource(int resourceId) {
        return getBuildingResources().stream().filter(buildingResource -> buildingResource.getId() == resourceId).findAny().get();
    }

    @Override
    public void reload() {

    }

    @Override
    public Class<?> getResourceClass() {
        return BuildingResource.class;
    }
}
