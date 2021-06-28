package com.mmorpg.qx.module.player.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import com.haipaite.common.ramcache.anno.InitialConfig;
import com.haipaite.common.ramcache.anno.InitialType;
import com.mmorpg.qx.module.player.model.Player;
import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.Cached;
import com.haipaite.common.ramcache.anno.Persister;

import java.util.Objects;

@Entity
@Cached(persister = @Persister("30s"))
@NamedQueries({@NamedQuery(name = "PlayerEnt.playerShortInfo", query = "SELECT playerId,account,serverId,name,role FROM PlayerEnt")})
public class PlayerEnt implements IEntity<Long> {
    @Id
    private long playerId;
    private String account;
    private int serverId;
    private String name;
    private int role;
    private int sex;
    private int level = 1;
    private long exp;
    private int head;

    // 提纯氪金
    private long pureKryptonGold;
    // 氪金
    private long kryptonGold;
    // 金币
    private long gold;
    // 代金币
    private long intsteadGold;
    /**
     * 登陆坐标
     */
    private int loginPositionType;
    private int gridId;
    private int mapId;
    private int instanceId;
    /** 关卡进度 */
    private int barrier;

    private transient Player player;

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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getLoginPositionType() {
        return loginPositionType;
    }

    public void setLoginPositionType(int loginPositionType) {
        this.loginPositionType = loginPositionType;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getGold() {
        return gold;
    }

    public void setGold(long gold) {
        this.gold = gold;
    }

    public long getPureKryptonGold() {
        return pureKryptonGold;
    }

    public void setPureKryptonGold(long pureKryptonGold) {
        this.pureKryptonGold = pureKryptonGold;
    }

    public long getKryptonGold() {
        return kryptonGold;
    }

    public void setKryptonGold(long kryptonGold) {
        this.kryptonGold = kryptonGold;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public long getIntsteadGold() {
        return intsteadGold;
    }

    public void setIntsteadGold(long intsteadGold) {
        this.intsteadGold = intsteadGold;
    }

    public int getBarrier() {
        return barrier;
    }

    public void setBarrier(int barrier) {
        this.barrier = barrier;
    }

    @Override
    public boolean serialize() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerEnt playerEnt = (PlayerEnt) o;
        return playerId == playerEnt.playerId &&
                serverId == playerEnt.serverId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, serverId);
    }
}
