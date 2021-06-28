package com.mmorpg.qx.module.worldMap.enums;

/**
 * @author wang ke
 * @description: 三维方向简单定义
 * @since 18:06 2020-07-30
 */
public enum DirType {
    //X正方向 →
    XP(1, 300){
        @Override
        public DirType getOpposite() {
            return XM;
        }
    },
    //x负方向 ←
    XM(2, 300){
        @Override
        public DirType getOpposite() {
            return XP;
        }
    },
    // y↑
    YP(3, 300){
        @Override
        public DirType getOpposite() {
            return YM;
        }
    },
    //y↓
    YM(4, 300){
        @Override
        public DirType getOpposite() {
            return YP;
        }
    },
    //z↑
    ZP(5, 400){
        @Override
        public DirType getOpposite() {
            return ZM;
        }
    },
    //z↓
    ZM(6, 400){
        @Override
        public DirType getOpposite() {
            return ZP;
        }
    },
    //XY方向第一象限 ↗
    XY1(7, 300){
        @Override
        public DirType getOpposite() {
            return XY3;
        }
    },
    //XY方向第二象限 ↘
    XY2(8, 300){
        @Override
        public DirType getOpposite() {
            return XY4;
        }
    },
    //XY方向第三象限 ↙
    XY3(9, 300){
        @Override
        public DirType getOpposite() {
            return XY1;
        }
    },
    //XY方向第二象限 ↖
    XY4(10, 300){
        @Override
        public DirType getOpposite() {
            return XY2;
        }
    },
    ;
    //沿着指定方向走一格花费时间（毫秒）
    private short cost;
    private byte dir;

    private DirType(int dir, int cost){
        this.dir = (byte) dir;
        this.cost = (short)cost;
    }
    public abstract DirType getOpposite();

    public byte getDir(){
        return this.dir;
    }

    public static DirType indexOfType(int dir) {
        for (DirType de : values()) {
            if (de.getDir() == dir) {
                return de;
            }
        }
        throw new RuntimeException("not found this ordinal[" + dir + "]");
    }

    public short getCost() {
        return cost;
    }
}
