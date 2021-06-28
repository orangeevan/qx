package com.mmorpg.qx.module.object.gameobject.building;

import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.AbstractVisibleObjectController;
import com.mmorpg.qx.module.object.controllers.BuildingController;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;

/**
 * @author wang ke
 * @description: 祭坛
 * @since 14:54 2020-08-31
 */
public class BuildingAltarCreature extends AbstractBuilding{

    /**
     * Constructor.
     *
     * @param objId
     * @param controller
     * @param position
     */
    public BuildingAltarCreature(long objId, BuildingController controller, WorldPosition position) {
        super(objId, controller, position);
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.BUILDING_ALTAR;
    }
}
