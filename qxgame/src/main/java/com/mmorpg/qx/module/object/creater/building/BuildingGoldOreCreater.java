package com.mmorpg.qx.module.object.creater.building;

import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.module.building.resource.BuildingResource;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.BuildingController;
import com.mmorpg.qx.module.object.creater.AbstractBuildingCreater;
import com.mmorpg.qx.module.object.gameobject.building.BuildingGoldOreCreature;
import com.mmorpg.qx.module.object.spawn.resource.MapCreatureResource;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author wang ke
 * @description:
 * @since 16:32 2020-08-31
 */
@Component
public class BuildingGoldOreCreater extends AbstractBuildingCreater<BuildingGoldOreCreature> {

    @Override
    public BuildingGoldOreCreature create(MapCreatureResource sresource, BuildingResource resource, WorldMapInstance instance, Map<String, Object> args) {
        BuildingGoldOreCreature creature = new BuildingGoldOreCreature(IdentifyManager.getInstance().getNextIdentify(IdentifyManager.IdentifyType.MAP_CREATURE), new BuildingController(),
                WorldPosition.buildWorldPosition(instance, sresource.getGridId()));
        init(creature, sresource, resource);
        return creature;
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.BUILDING_GOLD_ORE;
    }
}
