package com.mmorpg.qx.module.troop.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 * @author zhang peng
 * @description
 * @since 15:11 2021/4/20
 */
@Resource
public class TroopUnlockResource {

    // 唯一ID
    @Id
    private int id;
    // 卡组类型
    private int type;
    // 卡组索引
    private int index;
    // 卡组默认名称
    private String name;
    // 卡组解锁消耗货币ID
    private int moneyId;
    // 卡组解锁消耗货币数量
    private int moneyNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoneyId() {
        return moneyId;
    }

    public void setMoneyId(int moneyId) {
        this.moneyId = moneyId;
    }

    public int getMoneyNum() {
        return moneyNum;
    }

    public void setMoneyNum(int moneyNum) {
        this.moneyNum = moneyNum;
    }
}
