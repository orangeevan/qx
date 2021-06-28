package com.mmorpg.qx.module.rank.model;

import com.mmorpg.qx.module.rank.model.value.IRankingValue;
import com.mmorpg.qx.module.rank.packet.vo.IRankingItemVo;

import java.util.Objects;

/**
 * 功能排行榜父类
 *
 * @param
 */
public abstract class IRankingObject<PK, V extends IRankingValue> implements IRank<PK, V> {

    // 唯一标志
    protected PK id;

    // 排行榜数值
    protected V value;

    @Override
    public PK getId() {
        return id;
    }

    @Override
    public V getValue() {
        return value;
    }

    /* @Override
     public int compareTo(T o) {
         if (Objects.isNull(o)) {
             return 1;
         }
         return this.value.compareTo(o.value);
     }*/
    public abstract <VO extends IRankingItemVo> VO convert();

    public void setId(PK id) {
        this.id = id;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IRankingObject)) return false;
        IRankingObject that = (IRankingObject) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
