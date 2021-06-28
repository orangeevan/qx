package com.mmorpg.qx.module.troop.entity;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.Persister;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * @author zhang peng
 * @since 16:30 2021/5/11
 */
@Entity
@Cached(persister = @Persister("30s"))
public class TroopEntity implements IEntity<Long> {

    @Id
    private long playerId;
    @Lob
    private String TroopStorage;

    @Override
    public Long getId() {
        return playerId;
    }

    @Override
    public boolean serialize() {
        return true;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public String getTroopStorage() {
        return TroopStorage;
    }

    public void setTroopStorage(String troopStorage) {
        TroopStorage = troopStorage;
    }
}
