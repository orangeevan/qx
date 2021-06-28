package com.mmorpg.qx.module.object.controllers.move;

import com.mmorpg.qx.module.worldMap.model.WorldPosition;

public class Road {
    /**
     * 方向数组
     */
    private final byte[] roads;
    /**
     * 总路径，验证用
     */
    private final int totalLength;
    /**
     * 当前位
     */
    private int index = 0;

    /**
     * 目标格子
     */
    private transient WorldPosition targetPosition;

    private Road(byte[] roads) {
        if (roads == null) {
            this.roads = new byte[]{};
            this.totalLength = 0;
        } else {
            this.roads = roads;
            this.totalLength = roads.length;
        }
    }

    public boolean isOver() {
        return index >= totalLength;
    }

    public byte[] getLeftRoads() {
        byte[] result = null;
        int length = totalLength - index;
        if (length > 0) {
            result = new byte[length];
            System.arraycopy(getRoads(), index, result, 0, length);
        }
        return result;
    }

    public static Road valueOf(byte... roads) {
        Road road = new Road(roads);
        return road;
    }

    public byte poll() {
        return roads[index++];
    }

    public byte[] getRoads() {
        return roads;
    }

    public WorldPosition getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(WorldPosition targetPosition) {
        this.targetPosition = targetPosition;
    }

    public byte getLastRoad(){
        return roads[roads.length -1];
    }
}
