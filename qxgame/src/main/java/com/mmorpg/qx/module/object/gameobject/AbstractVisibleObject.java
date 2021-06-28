package com.mmorpg.qx.module.object.gameobject;

import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.AbstractVisibleObjectController;
//import com.mmorpg.qx.module.object.knowlist.KnownList;
import com.mmorpg.qx.module.object.spawn.MapCreatureManager;
import com.mmorpg.qx.module.object.spawn.resource.MapCreatureResource;

import com.mmorpg.qx.module.worldMap.enums.DirType;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;

/**
 * 可见物
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public abstract class AbstractVisibleObject extends AbstractObject {

    /**
     * 观察者列表
     */
    //private KnownList knownlist;
    /**
     * 位置
     */
    private WorldPosition position;
    /**
     * 控制器
     */
    private AbstractVisibleObjectController<? extends AbstractVisibleObject> controller;

    /**
     * Visible object's target
     */
    private AbstractVisibleObject target;

    private int spawnKey;

    private int objectKey;

    private int bornGrid;

    private DirType dir;

    /**
     * Constructor.
     *
     * @param objId
     * @param controller
     */
    public AbstractVisibleObject(long objId, AbstractVisibleObjectController<? extends AbstractVisibleObject> controller, WorldPosition position) {
        this.objectId = objId;
        this.setController(controller);
        this.position = position;
        //this.knownlist = new KnownList(this);
    }

    /**
     * Returns current WorldRegion AionObject is in.
     *
     * @return mapRegion
     */

    public int getInstanceId() {
        return position != null ? position.getInstanceId() : 0;
    }

    /**
     * Return World map id.
     *
     * @return world map id
     */
    public int getMapId() {
        return position != null ? position.getMapId() : 0;
    }

    /**
     * Return World position x
     *
     * @return x
     */
    public int getGridId() {
        return position != null ? position.getGridId() : 0;
    }


    /**
     * Return object position
     *
     * @return position.
     */
    public WorldPosition getPosition() {
        return position;
    }

    /**
     * Check if object is spawned.
     *
     * @return true if object is spawned.
     */
//    public boolean isSpawned() {
//        return position.isSpawned();
//    }

//    public void clearKnownlist() {
//        getKnownList().clear();
//    }
//
//    public void updateKnownlist() {
//        getKnownList().doUpdate();
//    }

    /**
     * Set KnownList to this VisibleObject
     *
     * @param knownlist
     */
//    public void setKnownlist(KnownList knownlist) {
//        this.knownlist = knownlist;
//    }

    /**
     * Returns KnownList of this VisibleObject.
     *
     * @return knownList.
     */
//    public KnownList getKnownList() {
//        return knownlist;
//    }

    /**
     * Return VisibleObjectController of this VisibleObject
     *
     * @return VisibleObjectController.
     */
    public AbstractVisibleObjectController<? extends AbstractVisibleObject> getController() {
        return controller;
    }

    /**
     * @return VisibleObject
     */
    public AbstractVisibleObject getTarget() {
        return target;
    }

    /**
     * @param creature
     */
    public void setTarget(AbstractVisibleObject creature) {
        target = creature;
    }

    public MapCreatureResource getSpawn() {
        MapCreatureResource result = null;
        if (getSpawnKey() != 0) {
            result = MapCreatureManager.getInstance().getSpawn(getSpawnKey());
        }
        return result;
    }

    /**
     * @param objectId
     * @return target is object with id equal to objectId
     */
    public boolean isTargeting(int objectId) {
        return target != null && target.getObjectId().longValue() == objectId;
    }


    /**
     * 这是一个特殊的方法，如果这个方法想要有效必须保证一下语义 有两个对象 obj1 obje2 obj1.cansee(obj2) ==
     * obj2.cansee(obj1) 否则，将会发生一些不可预知的显示错误
     *
     * @param visibleObject
     * @return
     */
    public boolean canSee(AbstractVisibleObject visibleObject) {
        int v1 = ObjectType.CAN_SEE[getObjectType().getValue()][visibleObject.getObjectType().getValue()];
        int v2 = ObjectType.CAN_SEE[visibleObject.getObjectType().getValue()][getObjectType().getValue()];
        return Math.min(v1, v2) == 1;
    }

    public abstract ObjectType getObjectType();

    public boolean isObjectType(ObjectType objectType) {
        return getObjectType().equals(objectType);
    }

    public DirType getDir() {
        return dir;
    }

    public void setDir(DirType dir) {
        this.dir = dir;
    }

    public void setController(AbstractVisibleObjectController<? extends AbstractVisibleObject> controller) {
        this.controller = controller;
    }

    public void setPosition(WorldPosition position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] [%d] [%d] [%d] objectKey[%s] spawnKey[%s]", getObjectType(), getName(), getMapId(),
                getGridId(), getInstanceId(), getObjectKey(), getSpawnKey());
    }

    public int getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(int objectKey) {
        this.objectKey = objectKey;
    }

    public int getSpawnKey() {
        return spawnKey;
    }

    public void setSpawnKey(int spawnKey) {
        this.spawnKey = spawnKey;
    }

    public WorldMapInstance getWorldMapInstance() {
        if(position == null){
            return null;
        }
        return WorldMapService.getInstance().getWorldMapInstance(position.getMapId(), position.getInstanceId());
    }

    /***
     * 判断对象是否在地图上
     * @return
     */
    public boolean isInWorldMap() {
        if (position == null) {
            return false;
        }
//        if (!position.isSpawned()) {
//            return false;
//        }
        return true;
    }

    public boolean isInPosition(int grid) {
        if (!isInWorldMap()) {
            return false;
        }
        if (position.getGridId() != grid) {
            return false;
        }
        return true;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public int getZ() {
        return position.getZ();
    }
}