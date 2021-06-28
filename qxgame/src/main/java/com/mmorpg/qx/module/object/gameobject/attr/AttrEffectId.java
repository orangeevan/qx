package com.mmorpg.qx.module.object.gameobject.attr;

/**
 * 属性标识对象. 如果id相同的属性.会相互替换.
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public class AttrEffectId implements Comparable<AttrEffectId> {

    private AttrEffectType type;

    /**
     * 如果同类型下具体参数不同，认为不同模块
     */
    private Object param;

    /**
     * 不统计到战斗力中
     */
    private boolean noBattleScore;

    /**
     * 所有同一个模块下需要区分参数，比如效果模块不同效果
     * @param type
     * @param param
     * @return
     */
    public static AttrEffectId valueOf(AttrEffectType type, Object param) {
        AttrEffectId statEffectId = new AttrEffectId();
        statEffectId.type = type;
        statEffectId.param = param;
        return statEffectId;
    }

    public static AttrEffectId valueOf(AttrEffectType type) {
        return valueOf(type, null);
    }

    public static AttrEffectId valueOf(AttrEffectType type, boolean noBattleScore) {
        AttrEffectId statEffectId = new AttrEffectId();
        statEffectId.type = type;
        statEffectId.noBattleScore = noBattleScore;
        return statEffectId;
    }

    @Override
    public int compareTo(AttrEffectId o) {
        return this.type.compareTo(o.type);
    }

    public boolean isNoBattleScore() {
        return noBattleScore;
    }

    public void setNoBattleScore(boolean noBattleScore) {
        this.noBattleScore = noBattleScore;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((param == null) ? 0 : param.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AttrEffectId other = (AttrEffectId) obj;
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (param == null) {
            if (other.param != null) {
                return false;
            }
        } else if (!param.equals(other.param)) {
            return false;
        }
        return true;
    }

    public AttrEffectType getType() {
        return type;
    }
}
