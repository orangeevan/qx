package com.mmorpg.qx.module.integral.enums;

/**
 * @author zhang peng
 * @description:
 * @since 17:17 2021/3/5
 */
public enum  IntegralType {

    /** 体力 */
    PHYSICAL_POWER(10),
    /** 魔物娘经验 */
    MWN_EXP(11);


    IntegralType(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static IntegralType typeOf(int code) {
        for (IntegralType type : IntegralType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
