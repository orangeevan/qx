package com.mmorpg.qx.module.object;

import java.util.Arrays;
import java.util.Optional;

/**
 * 生物类型
 *
 * @author wang ke
 * @since v1.0 2018年2月24日
 */
public enum ObjectType {
    /**
     * NPC
     **/
    NPC(0),
    /**
     * 怪物
     **/
    MONSTER(1),
    /**
     * 魔物娘
     **/
    MWN(2),
    /**
     * 玩家
     **/
    PLAYER(3),
    /**
     * 机器驯养师
     */
    ROBOT_TRAINER(4),
    /**
     * Boss
     */
    BOSS(5),
    /**
     * 物品
     */
    ITEM(6),
    /**
     * 玩家驯养师存活在战斗场景
     */
    PLAYER_TRAINER(7),
    /**
     * 建筑圣堂
     */
    BUILDING_SHRINE(8),
    /**
     * 建筑炮塔
     */
    BUILDING_TOWER(9),
    /**
     * 建筑医疗帐篷
     */
    BUILDING_MEDICAL(10),
    /**
     * 建筑祭坛
     */
    BUILDING_ALTAR(11),
    /**
     * 建筑金矿
     */
    BUILDING_GOLD_ORE(12),
    /**
     * 火
     */
    FIRE(13),
    /**
     * 装备
     */
    Equip(14),
    ;
    /**
     * 两个对象相互之间看见关系,1=能看见,0=不能看见,2=无意义取对面数据(因为满足CAN_SEE[m][n] == CAN_SEE[n][m]).
     */
    public static int[][] CAN_SEE = {
            // 0-1--2--3--4--5
            {0, 0, 0, 1, 1, 0, 0}, // 0
            {2, 0, 0, 1, 1, 0, 0}, // 1
            {2, 2, 0, 1, 1, 1, 0}, // 2
            {1, 1, 1, 1, 1, 1, 1}, // 3
            {1, 1, 1, 1, 1, 1, 0}, // 4
            {1, 1, 1, 1, 1, 2, 0}, // 5
    };

    private final int value;

    private ObjectType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ObjectType[] getBuildType() {
        return new ObjectType[]{BUILDING_ALTAR, BUILDING_SHRINE, BUILDING_TOWER, BUILDING_MEDICAL, BUILDING_ALTAR, BUILDING_GOLD_ORE};
    }

    public static ObjectType valueOf(int id) {
        Optional<ObjectType> first = Arrays.stream(ObjectType.values()).filter(type -> type.value == id).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        throw new IllegalArgumentException("系统不支持的对象类型： " + id);
    }
}
