package com.mmorpg.qx.module.mwn.entity;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.Persister;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;


/**
 * @author wang ke
 * @description:魔物娘存储实体
 * @since 21:27 2020-08-12
 */
@Entity
@Cached(persister = @Persister("30s"))
public class PlayerMwnEntity implements IEntity<Long> {

    /**
     * 归属玩家ID
     */
    @Id
    private long playerId;
    /**
     * 魔物娘数据
     */
    @Lob
    private String mwnJson;

    @Override
    public Long getId() {
        return playerId;
    }

    @Override
    public boolean serialize() {
        return true;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getMwnJson() {
        return mwnJson;
    }

    public void setMwnJson(String mwnJson) {
        this.mwnJson = mwnJson;
    }
}
