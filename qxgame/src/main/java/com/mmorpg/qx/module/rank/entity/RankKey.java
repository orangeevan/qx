package com.mmorpg.qx.module.rank.entity;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.rank.enums.RankSubType;
import com.mmorpg.qx.module.rank.enums.RankType;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * @author: yuanchengyan
 * @description:
 * @since 11:53 2021/4/7
 */
@Embeddable
public class RankKey implements Serializable, Comparable<RankKey> {
    @Protobuf
    @Enumerated(EnumType.STRING)
    private RankType type;
    @Protobuf
    @Enumerated(EnumType.STRING)
    private RankSubType subType;

    public static RankKey valueOf(RankType type, RankSubType subType) {
        RankKey key = new RankKey();
        key.type = type;
        key.subType = subType;
        return key;
    }


    public RankType getType() {
        return type;
    }

    public void setType(RankType type) {
        this.type = type;
    }

    public RankSubType getSubType() {
        return subType;
    }

    public void setSubType(RankSubType subType) {
        this.subType = subType;
    }

    @Override
    public int hashCode() {
        return type.getId() * 31 + subType.ordinal();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RankKey)) {
            return false;
        }
        RankKey rankKey = (RankKey) obj;
        return type == rankKey.type && subType == rankKey.subType;
    }

    @Override
    public int compareTo(RankKey o) {
        int c = this.type.compareTo(o.type);
        if (c != 0) {
            return c;
        }
        return this.subType.compareTo(o.subType);
    }
}

