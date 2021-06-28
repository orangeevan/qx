package com.mmorpg.qx.module.trainer.module;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.trainer.manager.TrainerManager;

import java.util.List;

/**
 * @author wang ke
 * @description: 玩家驯养师
 * @since 15:40 2020-08-13
 */
public class PlayerTrainer {

    private transient Player owner;

    private long trainerId;

    private int resourceId;

    private String name;

    private long exp;

    private int skinResourceId;

    // 拥有的皮肤
    private List<Integer> skinIdList;

    private boolean inFight;

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(long trainerId) {
        this.trainerId = trainerId;
    }

    @JSONField(serialize = false)
    public List<Attr> getAttrList() {
        return TrainerManager.getInstance().getPlayerTrainerResource(resourceId).getBaseAttrsList();
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
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

    public int getSkinResourceId() {
        return skinResourceId;
    }

    public void setSkinResourceId(int skinResourceId) {
        this.skinResourceId = skinResourceId;
    }

    public List<Integer> getSkinIdList() {
        return skinIdList;
    }

    public void setSkinIdList(List<Integer> skinIdList) {
        this.skinIdList = skinIdList;
    }

    public boolean isInFight() {
        return inFight;
    }

    public void setInFight(boolean inFight) {
        this.inFight = inFight;
    }
}
