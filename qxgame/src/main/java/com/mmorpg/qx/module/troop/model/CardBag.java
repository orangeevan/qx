package com.mmorpg.qx.module.troop.model;

import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 卡包对象, 进入战场时带入, 小型容器
 * @since 17:32 2020-08-06
 */
public class CardBag {

    private transient AbstractTrainerCreature owner;

    private List<MoWuNiang> mwns;

    private List<Card> cards;

    /** 编队卡牌数量限制 */
    private int size;

    private int skillId;

    /** 魔物娘平均等级 */
    private int mwnAvgLv;

    public CardBag() {
        this(30);
    }

    public CardBag(int size) {
        this.size = size;
        this.mwns = new ArrayList<>();
        this.cards = new ArrayList<>();
    }

    public CardBag(List<MoWuNiang> mwns, List<Card> cards, int size, int skillId) {
        this.mwns = mwns;
        this.cards = cards;
        this.size = size;
        this.skillId = skillId;
    }

    public MoWuNiang getByIndex(int index) {
        return mwns.get(index);
    }

    public MoWuNiang getById(long mwnId){
        return mwns.stream().filter(mwn -> mwn.getId() == mwnId).findFirst().orElse(null);
    }

    public MoWuNiang remove(int index) {
        return mwns.remove(index);
    }

    public void removeMwn(MoWuNiang mwn){
        mwns.remove(mwn);
        Card card = getCard(mwn);
        if (card != null) {
            cards.remove(getCard(mwn));
        }
    }

    public int getCurrentSize() {
        return mwns.size();
    }

    public boolean isFull() {
        return mwns.size() >= this.size;
    }

    public boolean addMwn(MoWuNiang mwn) {
        if (mwns.size() >= this.size) {
            return false;
        }
        mwns.add(mwn);
        Card card = getCard(mwn);
        if (card == null) {
            card = Card.valueOf(mwn.getId(), 0, 0, 0);
        }
        cards.add(card);
        mwnAvgLv = (int) mwns.stream().mapToInt(MoWuNiang::getLevel).average().getAsDouble();
        return true;
    }

    public void reduceMwn(int resourceId) {
        Optional<MoWuNiang> any = mwns.stream().filter(moWuNiang -> moWuNiang.getResourceId() == resourceId).findAny();
        any.ifPresent(moWuNiang -> mwns.remove(moWuNiang));
    }

    public void reduceMwn(long objectId) {
        Optional<MoWuNiang> any = mwns.stream().filter(moWuNiang -> moWuNiang.getId() == objectId).findAny();
        any.ifPresent(moWuNiang -> mwns.remove(moWuNiang));
    }

    public boolean hasMwn(long mwnId) {
        if (CollectionUtils.isEmpty(mwns)) {
            return false;
        }
        return mwns.stream().anyMatch(moWuNiang -> moWuNiang.getId() == mwnId);
    }

    public Card getCard(MoWuNiang mwn) {
        return cards.stream().filter(t -> t.getMwnId() == mwn.getId()).findAny().orElse(null);
    }

    /**
     * 复制卡包
     */
    public CardBag deepCopy() {
        CardBag cardBag = new CardBag();
        cardBag.mwns = this.mwns.stream().map(MoWuNiang::copy).collect(Collectors.toList());
        cardBag.cards = this.cards.stream().map(Card::copy).collect(Collectors.toList());
        cardBag.size = this.size;
        cardBag.skillId = this.skillId;
        return cardBag;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<MoWuNiang> getMwns() {
        return mwns;
    }

    public void setMwns(List<MoWuNiang> mwns) {
        this.mwns = mwns;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMwnAvgLv() {
        return mwnAvgLv;
    }

    public void setMwnAvgLv(int mwnAvgLv) {
        this.mwnAvgLv = mwnAvgLv;
    }

    public AbstractTrainerCreature getOwner() {
        return owner;
    }

    public void setOwner(AbstractTrainerCreature owner) {
        this.owner = owner;
    }
}
