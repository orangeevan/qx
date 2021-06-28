package com.mmorpg.qx.module.mwn.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.gameobject.packet.vo.AttrVo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description:魔物娘通信VO
 * @since 20:00 2020-09-21
 */
public class MoWuNiangVo {

    @Protobuf(description = "魔物娘id")
    private long id;

    /**
     * 关联配置数据ID
     */
    @Protobuf(description = "魔物娘配置id")
    private int resourceId;

    /***
     * 属性数据
     */
    @Protobuf(description = "魔物娘属性")
    private List<AttrVo> attrList;

    @Protobuf(description = "魔物娘皮肤id")
    private int skinResourceId;

    @Protobuf(description = "魔物娘等级")
    private int level;

    @Protobuf(description = "魔物娘等级经验")
    private int exp;

    @Protobuf(description = "魔物娘星数")
    private int starLv;

    @Protobuf(description = "魔物娘阶数")
    private int casteLv;

    @Protobuf(description = "魔物娘羁绊等级")
    private int fetterLv;

    @Protobuf(description = "魔物娘羁绊经验")
    private int fetterExp;

    @Protobuf(description = "魔物娘是否处于疲劳状态 0：否 1：是")
    private int tired;

    @Protobuf(description = "已解锁的皮肤")
    private List<GameUtil.IntegerVo> skinIds;

    public static MoWuNiangVo valueOf(MoWuNiang mwn) {
        MoWuNiangVo vo = new MoWuNiangVo();
        vo.id = mwn.getId();
        vo.resourceId = mwn.getResourceId();
        vo.attrList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mwn.getAttrList())) {
            mwn.getAttrList().forEach(attr -> vo.attrList.add(AttrVo.valueOf(attr.getType().value(), attr.getValue())));
        }
        vo.skinResourceId = mwn.getSkinResourceId();
        vo.level = mwn.getLevel();
        vo.exp = mwn.getExp();
        vo.starLv = mwn.getStarLv();
        vo.casteLv = mwn.getCasteLv();
        vo.fetterLv = mwn.getFetterLv();
        vo.fetterExp = mwn.getFetterExp();
        vo.tired = mwn.hasThrowDice() ? 1 : 0;
        vo.skinIds = mwn.getSkinIds() == null ? new ArrayList<>() :
                mwn.getSkinIds().stream().map(GameUtil.IntegerVo::valueOf).collect(Collectors.toList());
        return vo;
    }

    public long getId() {
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

    public List<AttrVo> getAttrList() {
        return attrList;
    }

    public void setAttrList(List<AttrVo> attrList) {
        this.attrList = attrList;
    }

    public int getSkinResourceId() {
        return skinResourceId;
    }

    public void setSkinResourceId(int skinResourceId) {
        this.skinResourceId = skinResourceId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getTired() {
        return tired;
    }

    public void setTired(int tired) {
        this.tired = tired;
    }

    public List<GameUtil.IntegerVo> getSkinIds() {
        return skinIds;
    }

    public void setSkinIds(List<GameUtil.IntegerVo> skinIds) {
        this.skinIds = skinIds;
    }
}
