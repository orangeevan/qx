package com.mmorpg.qx.module.trainer.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.object.gameobject.packet.vo.AttrVo;
import com.mmorpg.qx.module.trainer.module.PlayerTrainer;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhang peng
 * @description
 * @since 10:25 2021/4/7
 */
public class PlayerTrainerVo {

    @Protobuf(description = "驯养师ID")
    private long trainerId;

    @Protobuf(description = "驯养师配置ID")
    private int resourceId;

    @Protobuf(description = "当前穿戴皮肤ID")
    private int skinId;

    @Protobuf(description = "驯养师名称")
    private String name;

    @Protobuf(description = "驯养师经验")
    private long exp;

    @Protobuf(description = "所有皮肤ID")
    private List<GameUtil.IntegerVo> skinIdList;

    @Protobuf(description = "驯养师属性")
    private List<AttrVo> attrList;

    @Protobuf(description = "当前是否出战 0否 1是")
    private int inFight;

    public static PlayerTrainerVo valueOf(PlayerTrainer trainer) {
        PlayerTrainerVo vo = new PlayerTrainerVo();
        vo.trainerId = trainer.getTrainerId();
        vo.resourceId = trainer.getResourceId();
        vo.skinId = trainer.getSkinResourceId();
        vo.name = trainer.getName();
        vo.exp = trainer.getExp();
        vo.skinIdList = CollectionUtils.isEmpty(trainer.getSkinIdList()) ? Collections.emptyList() :
                trainer.getSkinIdList().stream().map(GameUtil.IntegerVo::valueOf).collect(Collectors.toList());
        vo.attrList = CollectionUtils.isEmpty(trainer.getAttrList()) ? Collections.emptyList() : trainer.getAttrList().stream()
                .map(attr -> AttrVo.valueOf(attr.getType().value(), attr.getValue())).collect(Collectors.toList());
        vo.inFight = trainer.isInFight() ? 1 : 0;
        return vo;
    }

    public long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(long trainerId) {
        this.trainerId = trainerId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getSkinId() {
        return skinId;
    }

    public void setSkinId(int skinId) {
        this.skinId = skinId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public List<GameUtil.IntegerVo> getSkinIdList() {
        return skinIdList;
    }

    public void setSkinIdList(List<GameUtil.IntegerVo> skinIdList) {
        this.skinIdList = skinIdList;
    }

    public List<AttrVo> getAttrList() {
        return attrList;
    }

    public void setAttrList(List<AttrVo> attrList) {
        this.attrList = attrList;
    }

    public int getInFight() {
        return inFight;
    }

    public void setInFight(int inFight) {
        this.inFight = inFight;
    }
}
