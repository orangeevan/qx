package com.mmorpg.qx.module.match.enums;

/**
 * @author wang ke
 * @description: 匹配类型
 * @since 16:07 2020-08-12
 */
public enum MatchType {
    PLAYER(1, "真人匹配"),
    ROBOT(2, "机器人匹配"),
    TEAM(3, "组队匹配"),
    INVITE(4, "邀请个人比赛"),
    ;
    private int id;
    private String desc;
    private MatchType(int id, String desc){
        this.id = id;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
