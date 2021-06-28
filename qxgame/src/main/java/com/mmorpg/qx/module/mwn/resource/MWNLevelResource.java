package com.mmorpg.qx.module.mwn.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 * @author zhang peng
 * @description: 魔物娘等级配置表
 * @since 17:52 2021/3/11
 */
@Resource
public class MWNLevelResource {
    @Id
    private int id;
    // 消耗道具ID
    private int costId;
    // 单级经验
    private int fullExp;
    // 升级阶数限制
    private int casteLimit;
    // 1费血量 魔物娘费用
    private int hp;
    // 攻击
    private int attack;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCostId() {
        return costId;
    }

    public void setCostId(int costId) {
        this.costId = costId;
    }

    public int getFullExp() {
        return fullExp;
    }

    public void setFullExp(int fullExp) {
        this.fullExp = fullExp;
    }

    public int getCasteLimit() {
        return casteLimit;
    }

    public void setCasteLimit(int casteLimit) {
        this.casteLimit = casteLimit;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }
}
