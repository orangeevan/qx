package com.mmorpg.qx.module.player.entity;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.Persister;
import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.module.player.model.PlayerCommonType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wang ke
 * @description: 玩家通用数据
 * @since 15:14 2020-10-19
 */
@Entity
@Cached(persister = @Persister("30s"))
public class PlayerCommonEnt implements IEntity<Long> {

    @Id
    private long playerId;

    private String commomDataJson;

    private transient Map<PlayerCommonType, Integer> commonData;

    private transient AtomicBoolean change = new AtomicBoolean(false);

    @Override
    public Long getId() {
        return playerId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public Map<PlayerCommonType, Integer> getCommonData() {
        return commonData;
    }

    public void setCommonData(Map<PlayerCommonType, Integer> commonData) {
        this.commonData = commonData;
    }

    @Override
    public boolean serialize() {
        if (!CollectionUtils.isEmpty(commonData)) {
            commomDataJson = JsonUtils.map2String(commonData);
        }
        return true;
    }

    public void unSerialize() {
        if (!StringUtils.isEmpty(commomDataJson)) {
            commonData = JsonUtils.string2Map(commomDataJson, PlayerCommonType.class, Integer.class);
        }
    }

    public AtomicBoolean getChange() {
        return change;
    }
}
