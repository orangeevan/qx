package com.mmorpg.qx.module.object.gameobject.building;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.worldMap.enums.DirType;

/**
 * @author wang ke
 * @description:
 * @since 16:17 2020-08-31
 */

public class BuildingInfo {
    @Protobuf
    private long objectId;

    @Protobuf
    private int resourceId;

    @Protobuf
    private int gridId;

    @Protobuf
    private DirType dirType;

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }

    public DirType getDirType() {
        return dirType;
    }

    public void setDirType(DirType dirType) {
        this.dirType = dirType;
    }

    public static BuildingInfo valueOf(AbstractBuilding building) {
        BuildingInfo buildingInfo = new BuildingInfo();
        buildingInfo.setObjectId(building.getObjectId());
        buildingInfo.setResourceId(building.getResourceId());
        buildingInfo.setGridId(building.getPosition().getGridId());
        buildingInfo.setDirType(building.getDir());
        return buildingInfo;
    }
}
