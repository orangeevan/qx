package com.mmorpg.qx.module.skin.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;
import com.mmorpg.qx.module.skin.enums.SkinQa;

/**
 * @author wang ke
 * @description: 玩家驯养师皮肤配置
 * @since 18:57 2020-09-27
 */
@Resource
public class PlayerTrainerSkinResource {
    @Id
    private int id;

    private int playerTrainerId;

    private String name;

    private int skinQa;

    private int getWay;

    private SkinQa skinQaType;

    public int getPlayerTrainerId() {
        return playerTrainerId;
    }

    public void setPlayerTrainerId(int playerTrainerId) {
        this.playerTrainerId = playerTrainerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSkinQa() {
        return skinQa;
    }

    public void setSkinQa(int skinQa) {
        this.skinQa = skinQa;
    }

    public int getGetWay() {
        return getWay;
    }

    public void setGetWay(int getWay) {
        this.getWay = getWay;
    }

    public SkinQa getSkinQaType() {
        return skinQaType;
    }

    public void setSkinQaType(SkinQa skinQaType) {
        this.skinQaType = skinQaType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
