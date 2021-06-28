package com.mmorpg.qx.module.rank.entity;


import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.Persister;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 * @author wang ke
 * @description:
 * @since 18:55 2021/3/11
 */
@Entity
@Cached(persister = @Persister("30s"))
public class RankEntity implements IEntity<RankKey> {
    @EmbeddedId
    private RankKey key;

    @Lob
    private String jsonData;


    @Override
    public RankKey getId() {
        return key;
    }

    @Override
    public boolean serialize() {
        return true;
    }


    public RankKey getKey() {
        return key;
    }

    public void setKey(RankKey key) {
        this.key = key;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RankEntity)) {
            return false;
        }
        RankEntity entity = (RankEntity) obj;
        return entity.getKey().equals(this.key);
    }
}
