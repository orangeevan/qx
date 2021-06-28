package com.mmorpg.qx.module.troop.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 * @author zhang peng
 * @description
 * @since 10:14 2021/4/9
 */
@Resource
public class TroopNumResource {

    // 唯一ID
    @Id
    private int id;
    // 玩家等级
    private int level;
    // 卡牌数量
    private int cardNum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCardNum() {
        return cardNum;
    }

    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }
}
