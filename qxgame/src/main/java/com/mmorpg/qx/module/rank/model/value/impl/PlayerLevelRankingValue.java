package com.mmorpg.qx.module.rank.model.value.impl;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.rank.enums.WaveType;
import com.mmorpg.qx.module.rank.model.value.IRankingValue;

/**
 * @author: yuanchengyan
 * @description:
 * @since 11:15 2021/4/7
 */
public class PlayerLevelRankingValue implements IRankingValue<PlayerLevelRankingValue> {
    @Protobuf
    private int level;

    public static PlayerLevelRankingValue valueOf(int level) {
        PlayerLevelRankingValue value = new PlayerLevelRankingValue();
        value.level = level;
        return value;
    }

    @Override
    public WaveType compareTo(PlayerLevelRankingValue o) {
        int c = Integer.compare(level, o.level);
        if (c == 0) {
            return WaveType.NONE;
        }
        if (c > 0) {
            return WaveType.UP;
        }
        return WaveType.DOWM;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

