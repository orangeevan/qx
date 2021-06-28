package com.mmorpg.qx.module.object.gameobject;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * @author wang ke
 * @description: 驯养师玩家简短信息
 * @since 14:21 2020-12-08
 */
public class TrainerCreatureShortInfo {
    @Protobuf(description = "驯养师名称")
    private String name;

    public static TrainerCreatureShortInfo valueOf(AbstractTrainerCreature creature){
        TrainerCreatureShortInfo shortInfo = new TrainerCreatureShortInfo();
        shortInfo.name = creature.getName();
        return shortInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
