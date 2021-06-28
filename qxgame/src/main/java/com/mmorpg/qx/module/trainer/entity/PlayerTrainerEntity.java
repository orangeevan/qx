package com.mmorpg.qx.module.trainer.entity;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.Persister;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * @author wang ke
 * @description:玩家驯养师DB对象
 * @since 14:51 2020-08-13
 */
@Entity
@Cached(persister = @Persister("30s"))
public class PlayerTrainerEntity implements IEntity<Long> {
    @Id
    private long playerId;

    /**驯养师基本数据*/
    @Lob
    private String trainerJson;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getTrainerJson() {
        return trainerJson;
    }

    public void setTrainerJson(String trainerJson) {
        this.trainerJson = trainerJson;
    }

    @Override
    public Long getId() {
        return playerId;
    }

    @Override
    public boolean serialize() {
        return true;
    }
}
