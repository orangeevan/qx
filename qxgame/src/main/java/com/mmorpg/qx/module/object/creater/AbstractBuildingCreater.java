package com.mmorpg.qx.module.object.creater;

import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.module.building.resource.BuildingResource;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.building.AbstractBuilding;
import com.mmorpg.qx.module.object.spawn.resource.MapCreatureResource;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wang ke
 * @description: 建筑创建抽象工厂
 * @since 15:38 2020-09-28
 */
public abstract class AbstractBuildingCreater<T extends AbstractBuilding> {

    private static final Map<ObjectType, AbstractBuildingCreater> CREATER_MAP = new HashMap<ObjectType, AbstractBuildingCreater>();

    @Autowired
    protected IdentifyManager identifyManager;

    @PostConstruct
    protected void init() {
        CREATER_MAP.put(getObjectType(), this);
    }

    public abstract ObjectType getObjectType();


    public static AbstractBuildingCreater getCreater(ObjectType type) {
        return CREATER_MAP.get(type);
    }

    public abstract T create(MapCreatureResource mapResource, BuildingResource resource, WorldMapInstance instance, Map<String, Object> args);

    public void init(T building, MapCreatureResource mapResource, BuildingResource resource) {
        building.init(mapResource, resource, null);
    }
}
