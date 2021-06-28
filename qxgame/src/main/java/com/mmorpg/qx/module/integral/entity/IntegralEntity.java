package com.mmorpg.qx.module.integral.entity;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.Persister;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * @author zhang peng
 * @description: 积分实体类
 * @since 17:33 2021/3/4
 */
@Entity
@Cached(persister = @Persister("30s"))
public class IntegralEntity implements IEntity<Long> {

    @Id
    private long playerId;
    @Lob
    private String IntegralStore;

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

    public String getIntegralStore() {
        return IntegralStore;
    }

    public void setIntegralStore(String integralStore) {
        IntegralStore = integralStore;
    }
}
