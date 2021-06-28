package com.mmorpg.qx.module.object.gameobject.building;

import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.BuildingController;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;

/**
 * @author wang ke
 * @description: 金矿
 * @since 14:55 2020-08-31
 */
public class BuildingGoldOreCreature extends AbstractBuilding {

    /**
     * Constructor.
     *
     * @param objId
     * @param controller
     * @param position
     */
    public BuildingGoldOreCreature(long objId, BuildingController controller, WorldPosition position) {
        super(objId, controller, position);
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.BUILDING_GOLD_ORE;
    }
}
