package com.mmorpg.qx.module.object.gameobject.attr;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;

import java.util.*;

/**
 * @author wang ke
 * @since v1.0 2014-8-5
 */
public enum AttrType {

    /**
     * 最大血量
     */
    Max_Hp(0),
    /**
     * 最大魔法
     */
    Max_Mp(1),

    /** 职业类：领主、魔导、游侠、萨满、智者、战车、恶魔、工匠、盗贼 */
    /**
     * 领主
     */
    Job_Suzerain(2),
    /**
     * 魔导
     */
    Job_Magic(3),
    /**
     * 游侠
     */
    Job_Shooter(4),
    /**
     * 萨满
     */
    Job_Wizard(5),
    /**
     * 智者
     */
    Job_Wrise(6),
    /**
     * 战车
     */
    Job_Warcar(7),
    /**
     * 恶魔
     */
    Job_Evil(8),
    /**
     * 工匠
     */
    Job_Artizan(9),
    /**
     * 盗贼
     */
    Job_Thieves(10),
    /** 职业类 结束*/


    /** 元素类 风、火、地、水、光、暗 */
    /**
     * 风
     */
    Ele_Wind(11),
    /**
     * 火
     */
    Ele_Fire(12),
    /**
     * 地
     */
    Ele_Earth(13),
    /**
     * 水
     */
    Ele_Water(14),
    /**
     * 光
     */
    Ele_Light(15),
    /**
     * 暗
     */
    Ele_Dark(16),
    /** 元素类 结束 */

    /**
     * 攻击力
     */
    Attack(17),
    /**
     * 防御 暂时弃用
     */
    Defense(18),

    /**
     * 闪避
     */
    Miss(19),

    /**
     * 暴击率
     */
    Crit_Prob(20),

    /**
     * 免伤万分比
     */
    Avoid_Injury(21),

    /**
     * 吸血
     */
    Suck_Blood(22),

    /**
     * 伤害减免万分比
     */
    Harm_Reduce_Rate(23),

    /**
     * 伤害加成万分比
     */
    Harm_Add_Rate(24),

    /**
     * 命中率
     */
    Hit_Rate(25),

    /**
     * 添加敌方暴击率
     */
    Add_Eny_Crit(26),

    /**
     * 护甲
     */
    Defend(27),

    /**
     * 魔物娘武力，对驯养师造成伤害
     */
    Trainer_Harm(28),

    /**
     * 领域，脸朝向范围
     */
    Attack_Range_Face(29),

    /**
     *领域，脸朝向垂直方向范围
     */
    Attack_Range_Face_Ver(30),
//	/** 物理攻击上限 */
//	MAX_WU_ATTACK(2),
//	/** 物攻下限 */
//	MIN_WU_ATTACK(3),
//	/** 物防上限 */
//	MAX_WU_DEFEND(4),
//	/** 物防下限 */
//	MIN_WU_DEFEND(5),
//	/** 魔攻上限 */
//	MAX_MO_ATTACK(6),
//	/** 魔攻下限 */
//	MIN_MO_ATTACK(7),
//	/** 魔防上限 */
//	MAX_MO_DEFEND(8),
//	/** 魔防下限*/
//	MIN_MO_DEFEND(9),
//	/** 道攻上限*/
//	MAX_DAO_ATTACK(10),
//	/** 道攻下限 */
//	MIN_DAO_ATTACK(11),
//	/** 准确 */
//	EXACT(12),
//	/** 闪避 */
//	MISS(13),
//	/** 幸运 */
//	LUCK(14),
//	/** 诅咒 */
//	CURSE(15),
//	/** 暴击率 */
//	CRIT_PROB(16),
//	/** 暴击伤害抵抗 */
//	CRIT_HURT_DEFEND(17),
//	/** 暴击伤害免伤 */
//	CRIT_HURT_DERATE(18),

    ;

    private final int value;

    private AttrType(int value) {
        this.value = value;
    }

    /**
     * Return the integer value of this status code.
     */
    public int value() {
        return value;
    }

    /**
     * Return a string representation of this status code.
     */
    @Override
    public String toString() {
        return String.valueOf(name());
    }

    /**
     * Return the enum constant of this type with the specified numeric value.
     *
     * @param statusCode the numeric value of the enum to be returned
     * @return the enum constant with the specified numeric value
     * @throws IllegalArgumentException if this enum has no constant for the specified numeric value
     */
    public static AttrType valueOf(int statusCode) {
        for (AttrType status : values()) {
            if (status.value == statusCode) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
    }

    private static Map<String, AttrType> attrMap = new HashMap<>(AttrType.values().length);

    private static List<AttrType> jobsAttr = new ArrayList<>();

    private static List<AttrType> eleAttr = new ArrayList<>();

    public static final AttrType[] NEED_QUICK_SYNC_ATTR = {Max_Hp, Max_Mp, Attack};

    static {
        Arrays.stream(AttrType.values()).forEach(attrType -> {
            attrMap.put(attrType.name().toLowerCase(), attrType);
            if (attrType.name().startsWith("Job")) {
                jobsAttr.add(attrType);
            }
            if (attrType.name().startsWith("Ele")) {
                eleAttr.add(attrType);
            }
        });
    }

    public static boolean isAttrName(String name) {
        return attrMap.containsKey(name);
    }

    public static AttrType getByLowerName(String name) {
        return attrMap.get(name);
    }

    public int getAttrValue(AbstractCreature creature) {
        return creature.getAttrController().getCurrentAttr(this);
    }

    public static List<AttrType> getAllJobType() {
        return Collections.unmodifiableList(jobsAttr);
    }

    public static List<AttrType> getAllEleType() {
        return Collections.unmodifiableList(eleAttr);
    }

    public static boolean isJobOrEle(AttrType type) {
        return jobsAttr.contains(type) || eleAttr.contains(type);
    }

}
