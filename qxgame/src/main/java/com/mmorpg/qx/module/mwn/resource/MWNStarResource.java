package com.mmorpg.qx.module.mwn.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 * @author zhang peng
 * @description: 魔物娘潜能星数配置表
 * @since 19:35 2021/3/16
 */
@Resource
public class MWNStarResource {

    @Id
    private int id;
    // 魔物娘配置ID
    private int mwnKey;
    // 魔物娘星数
    private int starLv;
    // 消耗道具ID
    private int costId;
    // 消耗道具数量
    private int num;
    // 血量增加百分比
    private int addHpPercent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMwnKey() {
        return mwnKey;
    }

    public void setMwnKey(int mwnKey) {
        this.mwnKey = mwnKey;
    }

    public int getStarLv() {
        return starLv;
    }

    public void setStarLv(int starLv) {
        this.starLv = starLv;
    }

    public int getCostId() {
        return costId;
    }

    public void setCostId(int costId) {
        this.costId = costId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getAddHpPercent() {
        return addHpPercent;
    }

    public void setAddHpPercent(int addHpPercent) {
        this.addHpPercent = addHpPercent;
    }
}
