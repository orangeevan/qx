package com.mmorpg.qx.module.roundFight.model;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * @author wang ke
 * @description: 当前骰子点数
 * @since 17:37 2020-08-14
 */
public class DicePoint {
    /**
     * 回合数
     */
    private int round;

    @Protobuf(description = "骰子点数")
    private int points;

//    @Protobuf(description = "骰子2点数")
//    private int pointSecond;

    public static DicePoint valueOf(int points) {
        DicePoint dicePoint = new DicePoint();
        dicePoint.setPoints(points);
        return dicePoint;
    }

    public static DicePoint valueOf(int round, int points) {
        DicePoint dicePoint = new DicePoint();
        dicePoint.setRound(round);
        dicePoint.setPoints(points);
        return dicePoint;
    }

//    public int getPointFirst() {
//        return pointFirst;
//    }
//
//    public void setPointFirst(int pointFirst) {
//        this.pointFirst = pointFirst;
//    }
//
//    public int getPointSecond() {
//        return pointSecond;
//    }
//
//    public void setPointSecond(int pointSecond) {
//        this.pointSecond = pointSecond;
//    }


    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int calcPoints() {
        return points;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
