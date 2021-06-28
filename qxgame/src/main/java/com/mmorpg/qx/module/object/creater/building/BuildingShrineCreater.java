package com.mmorpg.qx.module.object.creater.building;

import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.module.building.resource.BuildingResource;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.BuildingController;
import com.mmorpg.qx.module.object.creater.AbstractBuildingCreater;
import com.mmorpg.qx.module.object.gameobject.building.BuildingShrineCreature;
import com.mmorpg.qx.module.object.spawn.resource.MapCreatureResource;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author wang ke
 * @description:建筑圣堂创建
 * @since 15:34 2020-08-31
 */
@Component
public class BuildingShrineCreater extends AbstractBuildingCreater<BuildingShrineCreature> {

    @Override
    public BuildingShrineCreature create(MapCreatureResource sresource, BuildingResource resource, WorldMapInstance instance, Map<String, Object> args) {
        BuildingShrineCreature creature = new BuildingShrineCreature(IdentifyManager.getInstance().getNextIdentify(IdentifyManager.IdentifyType.MAP_CREATURE), new BuildingController(), WorldPosition.buildWorldPosition(instance, sresource.getGridId()));
        init(creature, sresource, resource);
        return creature;
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.BUILDING_SHRINE;
    }


}
