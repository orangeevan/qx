package com.mmorpg.qx.module.rank.model.value;

import com.mmorpg.qx.module.rank.enums.WaveType;

/**
 * @author: yuanchengyan
 * @description:
 * @since 10:23 2021/4/7
 */
public interface IRankingValue<T extends IRankingValue> {
    WaveType compareTo(T o);
}

