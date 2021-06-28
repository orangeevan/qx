package com.mmorpg.qx.module.object.gameobject.attr;

/**
 * 属性
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public class Attr implements Cloneable {

    public final static int CARDINAL = 10000;

    /**
     * final 防止被意外修改
     */
    private final AttrType type;

    private final int value;

    @Override
    public Attr clone() {
        Attr property = new Attr(type, value);
        return property;
    }

    public Attr(AttrType type, int value) {
        this.type = type;
        this.value = value;
    }

    public static Attr valueOf(AttrType type, int value) {
        return new Attr(type, value);
    }

    public Attr multipleValue(float multiple) {
        return new Attr(this.getType(), (int) (this.getValue() * multiple));
    }

    public AttrType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Attr [type=" + type + ", value=" + value + "]";
    }


    public Attr reverse() {
        return Attr.valueOf(this.getType(), -this.getValue());
    }
}
