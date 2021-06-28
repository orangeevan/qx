package com.mmorpg.qx.module.object.spawn.resource;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.worldMap.enums.DirType;
import com.mmorpg.qx.module.worldMap.manager.MapResourceManager;
import com.mmorpg.qx.module.worldMap.model.WorldMap;

/**
 * 单独解析mapCreatures下json配置表
 *
 * @author wang ke
 * @since v1.0 2018年3月10日
 */
public class MapCreatureResource {
    /**
     * 策划配置的标识,可能重复.大部分地图编辑器没有填值,为空
     */
    private int key;
    /**
     * 地图id
     **/
    private int mapId;
    /**
     * 地图格子号
     */
    private int gridId;
    /**
     * 这个用来配置物种信息
     **/
    private int objectKey;
    /**
     * 是否需要重生
     **/
    private int respawn;
    /**
     * 尸体保留时间
     **/
    private int decayInterval = 1;
    /**
     * 重生时间
     **/
    private int spawnInterval = 3;



    /**
     * 警戒范围
     */
    private int warnrange;
    /**
     * 不自动生成
     */
    private boolean noAutoSpawn;
    /**
     * 朝向
     */
    private byte dir;
    /**
     * 生物类型
     */
    private ObjectType type;

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public void setRespawn(int respawn) {
        this.respawn = respawn;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(int objectKey) {
        this.objectKey = objectKey;
    }

    public int getDecayInterval() {
        return decayInterval;
    }

    public void setDecayInterval(int decayInterval) {
        this.decayInterval = decayInterval;
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }

    public void setSpawnInterval(int spawnInterval) {
        this.spawnInterval = spawnInterval;
    }


    private WorldMap getWorldMap() {
        return MapResourceManager.getInstance().getWorldMap(mapId);
    }

    public DirType createDir() {
        if (dir >= 0) {
            return DirType.indexOfType(Integer.valueOf(dir));
        }
        return DirType.indexOfType(GameUtil.getRandom().nextInt(DirType.values().length));
    }


    @Override
    public String toString() {
        return String.format("KEY : [%s]   MAPID : [%d]   grid :  [%d]    RANGE : [%d]", key, mapId, gridId, warnrange);
    }

    public int getWarnrange() {
        return warnrange;
    }

    public void setWarnrange(int warnrange) {
        this.warnrange = warnrange;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }

    public int getRespawn() {
        return respawn;
    }

    public boolean isNoAutoSpawn() {
        return noAutoSpawn;
    }

    public void setNoAutoSpawn(boolean noAutoSpawn) {
        this.noAutoSpawn = noAutoSpawn;
    }

    public byte getDir() {
        return dir;
    }

    public void setDir(byte dir) {
        this.dir = dir;
    }

    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }
}
