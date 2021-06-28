package com.mmorpg.qx.module.worldMap.model;

import com.mmorpg.qx.module.worldMap.enums.DirType;

/**
 * @author wang ke
 * @description: 格子坐标
 * @since 16:06 2021/4/12
 */
public class VirPoint {
    private int pointX;
    private int pointY;
    private DirType faceDir;

    public int getPointX() {
        return pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public DirType getFaceDir() {
        return faceDir;
    }

    public static VirPoint valueOf(int pointX, int pointY, DirType dir) {
        VirPoint virPoint = new VirPoint();
        virPoint.pointX = pointX;
        virPoint.pointY = pointY;
        virPoint.faceDir = dir;
        return virPoint;
    }
}
