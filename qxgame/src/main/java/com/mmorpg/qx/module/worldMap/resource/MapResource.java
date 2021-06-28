package com.mmorpg.qx.module.worldMap.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;
import com.mmorpg.qx.module.worldMap.enums.DirType;

@Resource
public class MapResource {
    @Id
    private int mapId;

    private String name;

    private String fileName;
    /**
     * 地图类型
     */
    private int type;

    /**
     * 玩家数量限制
     */
    private int maxPlayerNum;

    /**
     * 出生点
     */
    private int birthGrid;
    /**
     * 出生方向
     */
    private DirType birthDir;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMaxPlayerNum() {
        return maxPlayerNum;
    }

    public void setMaxPlayerNum(int maxPlayerNum) {
        this.maxPlayerNum = maxPlayerNum;
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
