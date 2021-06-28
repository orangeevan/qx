package com.mmorpg.qx.module.worldMap.model;

import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.worldMap.enums.DirType;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class WorldMap {

    private static final Logger log = SysLoggerFactory.getLogger(WorldMap.class);

    private AtomicInteger nextInstanceId = new AtomicInteger(0);

    /**
     *  缓存了地图生成实例，方便检索，地图业务逻辑走完需要移除
     */
    private Map<Integer, WorldMapInstance> instances = new ConcurrentHashMap<Integer, WorldMapInstance>();


    private int mapId;

    private String name;

    private Map<Integer, MapGrid> mapGrids;

    private int maxNum;

    /**
     * 出生点
     */
    private int birthGrid;

    /** 出生方向*/
    private DirType birthDir;

    public WorldMap() {
    }


    public WorldMapInstance getWorldMapInstanceById(int instanceId) {
        return getWorldMapInstance(instanceId);
    }

    private WorldMapInstance getWorldMapInstance(int instanceId) {
        return instances.get(instanceId);
    }

    public void addInstance(int instanceId, WorldMapInstance instance) {
        instances.put(instanceId, instance);
    }

    public int getNextInstanceId() {
        return nextInstanceId.incrementAndGet();
    }

    public Iterator<WorldMapInstance> iterator() {
        return instances.values().iterator();
    }

    public Map<Integer, MapGrid> getMapGrids() {
        return mapGrids;
    }

    public void setMapGrids(Map<Integer, MapGrid> mapGrids) {
        this.mapGrids = MapUtils.unmodifiableMap(mapGrids);
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public Map<Integer, WorldMapInstance> getInstances() {
        return instances;
    }


    /**
     * 通用默认选项规则
     *
     * @param player
     * @return
     */
    public WorldMapInstance selectOrCreateWorldMapInstance(Player player) {
        for (WorldMapInstance instance : instances.values()) {
            return instance;
        }
        return null;
    }

    public WorldMapInstance removeInstance(int instanceId){
        return instances.remove(instanceId);
    }

    /***
     * 获取地图格子
     */
    public MapGrid getMapGrid( int gridId){
        return mapGrids.get(gridId);
    }

    public int getBirthGrid() {
        return birthGrid;
    }

    public void setBirthGrid(int birthGrid) {
        this.birthGrid = birthGrid;
    }

    public DirType getBirthDir() {
        return birthDir;
    }

    public void setBirthDir(DirType birthDir) {
        this.birthDir = birthDir;
    }

}
