package com.mmorpg.qx.module.player.model;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * @author wang ke
 * @description: 玩家通用类型
 * @since 15:16 2020-10-19
 */
public enum PlayerCommonType {
    @Protobuf(description = "0:未开启简易战斗 1：开启简易战斗")
    MWN_SIMPLE_FIGHT(1,"魔物娘简易战斗设置"),
    ;

    private int id;
    private String desc;

    private PlayerCommonType(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

}
