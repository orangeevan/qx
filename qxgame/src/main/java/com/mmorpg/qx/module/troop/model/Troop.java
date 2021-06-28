package com.mmorpg.qx.module.troop.model;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.module.troop.manager.TroopSkillManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang peng
 * @description 编队对象
 * @since 14:24 2021/4/8
 */
public class Troop {

    @Protobuf(description = "编队类型 1普通 2天梯")
    private int type;

    @Protobuf(description = "编队索引")
    private int index;

    @Protobuf(description = "编队名字")
    private String name;

    @Protobuf(description = "卡牌列表")
    private List<Card> cards;

    @Protobuf(description = "是否出战 0 否 1是")
    private int fight;

    @Protobuf(description = "编队技能")
    private int skillId;

    public static Troop valueOf(int type, int index, String name) {
        Troop troop = new Troop();
        troop.type = type;
        troop.index = index;
        troop.name = name;
        troop.cards = new ArrayList<>();
        troop.fight = 0;
        troop.skillId = TroopSkillManager.getInstance().getDefaultSkill();
        return troop;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public int getFight() {
        return fight;
    }

    public void setFight(int fight) {
        this.fight = fight;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }
}
