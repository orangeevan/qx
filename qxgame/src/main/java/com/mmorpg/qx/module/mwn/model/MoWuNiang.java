package com.mmorpg.qx.module.mwn.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author wang ke
 * @description: 魔物娘信息，DB序列化对象，修改请注意
 * @since 21:27 2020-08-12
 */

public class MoWuNiang {

    private long id;

    /**
     * 魔物娘配置ID
     */
    private int resourceId;
    /**
     * 皮肤配置ID
     */
    private int skinResourceId;
    /** 属性数据 */
    //private List<Attr> attrList;
    /**
     * 等级
     */
    private int level = 1;
    /**
     * 经验
     */
    private int exp;
    /**
     * (突破)阶数
     */
    private int casteLv;
    /**
     * (潜能)星数
     */
    private int starLv;
    /**
     * 羁绊等级
     */
    private int fetterLv;
    /**
     * 羁绊经验
     */
    private int fetterExp;
    /**
     * 已解锁的皮肤
     */
    private List<Integer> skinIds;

    private volatile boolean hasThrowDice = false;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    @JSONField(serialize = false)
    public List<Attr> getAttrList() {
        if (this.resourceId < 0) {
            return new ArrayList<>();
        }
        return getResource().getBaseAttrsList();
    }

    public List<Integer> getSkinIds() {
        return skinIds;
    }

    public void setSkinIds(List<Integer> skinIds) {
        this.skinIds = skinIds;
    }

    public int getLevel() {
        return level;
    }

    public int getSkinResourceId() {
        return skinResourceId;
    }

    public void setSkinResourceId(int skinResourceId) {
        this.skinResourceId = skinResourceId;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @JSONField(serialize = false)
    public MWNResource getResource() {
        if (resourceId < 0) {
            return null;
        }
        return MWNManager.getInstance().getMWNResource(this.getResourceId());
    }

    public int getStarLv() {
        return starLv;
    }

    public void setStarLv(int starLv) {
        this.starLv = starLv;
    }

    public int getCasteLv() {
        return casteLv;
    }

    public void setCasteLv(int casteLv) {
        this.casteLv = casteLv;
    }

    public boolean hasThrowDice() {
        return hasThrowDice;
    }

    public void setHasThrowDice(boolean hasThrowDice) {
        this.hasThrowDice = hasThrowDice;
    }

    public int getFetterLv() {
        return fetterLv;
    }

    public void setFetterLv(int fetterLv) {
        this.fetterLv = fetterLv;
    }

    public int getFetterExp() {
        return fetterExp;
    }

    public void setFetterExp(int fetterExp) {
        this.fetterExp = fetterExp;
    }

    public void addExp(int exp) {
        this.exp += exp;
    }

    public void addOneLevel() {
        this.level++;
    }

    public void addOneCaste() {
        this.casteLv++;
    }

    public void addOneStar() {
        this.starLv++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MoWuNiang moWuNiang = (MoWuNiang) o;
        return id == moWuNiang.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * 拷贝对象
     *
     * @return
     */
    public MoWuNiang copy() {
        MoWuNiang moWuNiang = new MoWuNiang();
        moWuNiang.setId(this.getId());
        moWuNiang.setResourceId(this.getResourceId());
        moWuNiang.setExp(this.getExp());
        //moWuNiang.setAttrList(this.getAttrList());
        moWuNiang.setSkinResourceId(this.getSkinResourceId());
        moWuNiang.setLevel(this.getLevel());
        moWuNiang.setStarLv(this.getStarLv());
        moWuNiang.setCasteLv(this.getCasteLv());
        moWuNiang.setHasThrowDice(this.hasThrowDice);
        moWuNiang.setFetterLv(this.fetterLv);
        moWuNiang.setFetterExp(this.fetterExp);
        moWuNiang.setSkinIds(this.skinIds);
        return moWuNiang;
    }

    @JSONField(serialize = false)
    public int getAttrValue(AttrType type) {
        List<Attr> attrList = getAttrList();
        if (attrList.isEmpty()) {
            return 0;
        }
        return attrList.stream().filter(t -> t.getType() == type).findFirst().map(Attr::getValue).orElse(0);
    }
}
