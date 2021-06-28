package com.mmorpg.qx.module.rank.model;


import com.mmorpg.qx.module.rank.model.value.IRankingValue;

/**
 * 有序成员接口
 *
 * @param <T>
 */
public interface IRank<PK, V extends IRankingValue>/* extends Comparable<T>*/ {

    /**
     * 获取唯一ID
     */
    PK getId();

    /**
     * 获取数值
     */
    V getValue();

}
