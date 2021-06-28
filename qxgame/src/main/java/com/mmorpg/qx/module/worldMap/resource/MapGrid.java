package com.mmorpg.qx.module.worldMap.resource;

import com.haipaite.common.utility.JsonUtils;
import com.haipaite.common.utility.RandomUtils;
import com.mmorpg.qx.module.worldMap.enums.DirType;
import com.mmorpg.qx.module.worldMap.enums.GridType;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author wang ke
 * @description:地图格子数据
 * @since 17:57 2020-07-30
 */
public final class MapGrid {

    //地图格子号，同客户端一致，后面移动指定格子号
    private int id;

    //格子类型
    private int type;

    //相邻格子，有方向，方便检索
    private Map<DirType, Integer> neighbor;

    //坐标系X轴
    private int x2;

    //坐标系Y轴
    private int y2;

    //魔物娘放置格，魔物娘脸朝向反方向
    private int R;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<DirType, Integer> getNeighbor() {
        return neighbor;
    }

    public void setNeighbor(Map<DirType, Integer> neighbor) {
        this.neighbor = neighbor;
    }


    public static void main(String[] args) {
        ArrayList<MapGrid> mapGrids = new ArrayList<>();
        int i = 1;
        while (i < 100) {
            MapGrid mapGrid = new MapGrid();
            mapGrid.setId(i);
            //mapGrid.setType(GridType.COMMON);
            Map<DirType, Integer> neighbor = new HashMap<>();
            neighbor.put(DirType.XP, i + 1);
            mapGrid.neighbor = neighbor;
            //mapGrid.setExt("");
            mapGrids.add(mapGrid);

            MapGrid mapGridBind = new MapGrid();
            mapGridBind.setId(100 + i);
            //mapGridBind.setType(GridType.COMMON);
            Map<DirType, Integer> neighborBind = new HashMap<>();
            neighborBind.put(DirType.XM, i);
            mapGridBind.neighbor = neighborBind;
            //mapGridBind.setExt("");
            mapGrids.add(mapGridBind);
            i++;
        }
        System.err.println(JsonUtils.object2String(mapGrids));
    }

    /***
     * 是否有对应方向邻居
     */
    public boolean hasDirNeighbor(DirType dirType) {
        return neighbor.containsKey(dirType);
    }

    /**
     * 获取指定方向邻居格子号
     */
    public int getFixDirNeighbor(DirType dirType) {
        Integer gridId = neighbor.get(dirType);
        if (gridId == null) {
            return Integer.MIN_VALUE;
        }
        return gridId;
    }

    /**
     * 随机邻居方向
     *
     * @return
     */
    public DirType randomDirNeighbor(DirType filterDir) {
        if (CollectionUtils.isEmpty(neighbor)) {
            return null;
        }
        List<DirType> dirTypes = new ArrayList<>(neighbor.keySet());
        if (Objects.nonNull(filterDir) && dirTypes.contains(filterDir)) {
            dirTypes.remove(filterDir);
        }
        if (dirTypes.size() == 0) {
            return null;
        }
        if (dirTypes.size() == 1) {
            return dirTypes.get(0);
        }
        int index = RandomUtils.betweenInt(0, dirTypes.size() - 1, true);
        return dirTypes.get(index);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean canSetMwn() {
        return GridType.valueOf(type) == GridType.COMMON;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getR() {
        return R;
    }

    public void setR(int r) {
        R = r;
    }

    /**
     * 格子生物出生反朝向固定 （-90 左   90 右   180 后    0 前 ）
     *
     * @return
     */
    public DirType getBirthDir() {
        switch (R) {
            case -90:
                return DirType.XP;
            case 90:
                return DirType.XM;
            case 0:
                return DirType.YM;
            case 180:
                return DirType.YP;
            default:
                throw new RuntimeException(String.format("格子 [%d] 出生朝向 [] 配置异常: ", id, R));
        }
    }
}
