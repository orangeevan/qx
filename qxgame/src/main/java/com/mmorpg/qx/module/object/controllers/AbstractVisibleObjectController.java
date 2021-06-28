package com.mmorpg.qx.module.object.controllers;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;

/**
 * 抽象可见物控制器
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public abstract class AbstractVisibleObjectController<T extends AbstractVisibleObject> {
    private int mapId;

    private int mapInstanceId;

    private boolean isSpawn;

    /**
     * Object that is controlled by this controller.
     */
    private T owner;

    /**
     * Set owner (controller object).
     *
     * @param owner
     */
    public void setOwner(T owner) {
        this.owner = owner;
    }

    /**
     * Get owner (controller object).
     */
    public T getOwner() {
        return owner;
    }

    /**
     * Called when controlled object is seeing other VisibleObject.
     *
     * @param object
     */
    public void see(AbstractVisibleObject object) {

    }

    /**
     * Called when controlled object no longer see some other VisibleObject.
     *
     * @param object
     */
    public void notSee(AbstractVisibleObject object, boolean isOutOfRange) {

    }

    /**
     * Removes controlled object from the world.
     */
    public void delete() {
        //if (getOwner().isSpawned()) {
        WorldMapService.getInstance().removeObject(getOwner());
    }

    /**
     * Called when object is re-spawned
     */
    public void onRelive() {

    }

    public void onDie(AbstractCreature lastAttacker, int skillId, int effectId) {
    }

    public void onDespawn() {
        isSpawn = false;
        this.mapInstanceId = 0;
        this.mapId = 0;
    }

    public void onSpawn(int mapId, int instanceId) {
        this.mapId = mapId;
        this.mapInstanceId = instanceId;
        this.isSpawn = true;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public int getMapInstanceId() {
        return mapInstanceId;
    }

    public void setMapInstanceId(int mapInstanceId) {
        this.mapInstanceId = mapInstanceId;
    }

    public boolean isSpawn() {
        return isSpawn;
    }
}
