package com.mmorpg.qx.module.troop.service;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.configValue.ConfigValueManager;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.purse.service.PurseService;
import com.mmorpg.qx.module.troop.constant.TroopGoFight;
import com.mmorpg.qx.module.troop.manager.*;
import com.mmorpg.qx.module.troop.model.Card;
import com.mmorpg.qx.module.troop.model.CardBag;
import com.mmorpg.qx.module.troop.model.Troop;
import com.mmorpg.qx.module.troop.packet.resp.EditTroopNameResp;
import com.mmorpg.qx.module.troop.packet.resp.TroopChangeSkillResp;
import com.mmorpg.qx.module.troop.packet.resp.TroopGoFightResp;
import com.mmorpg.qx.module.troop.packet.resp.UpdateTroopResp;
import com.mmorpg.qx.module.troop.resource.TroopUnlockResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhang peng
 * @since 15:50 2021/5/11
 */
@Component
public class TroopService {

    @Autowired
    private TroopManager troopManager;
    @Autowired
    private TroopNumManager troopNumManager;
    @Autowired
    private TroopUnlockManager troopUnlockManager;
    @Autowired
    private TroopWeightManager troopWeightManager;
    @Autowired
    private TroopStageManager troopStageManager;

    public static TroopService getInstance() {
        return BeanService.getBean(TroopService.class);
    }

    /**
     * 解锁编队
     *
     *  @param player
     * @param type
     * @param index
     * @param name
     */
    public void unlockTroop(Player player, int type, int index, String name) {
        List<Troop> troops = player.getTroopStorage().getTroops();
        // 检查编队数量
        if (troops.stream().filter(troop -> troop.getType() == type).count() >= troopNumManager.getTroopNumLimit()) {
            throw new ManagedException(ManagedErrorCode.TROOP_NUM_LIMIT);
        }
        if (getTroop(player, type, index) != null) {
            throw new ManagedException(ManagedErrorCode.TROOP_IS_UNLOCK);
        }
        // 检查编队名字长度 最多7个中文字符（14个英文字符）
        int length = getLength(name);
        if (length == 0 || length > 14) {
            throw new ManagedException(ManagedErrorCode.TROOP_NAME_LENGTH_LIMIT);
        }
        TroopUnlockResource resource = troopUnlockManager.getResource(type, index);
        if (resource == null) {
            throw new ManagedException(ManagedErrorCode.SYS_ERROR);
        }
        // 消耗货币
        PurseService.getInstance().consumePurse(player, String.valueOf(resource.getMoneyId()), resource.getMoneyNum());
        Troop troop = Troop.valueOf(type, index, name);
        troops.add(troop);
        troopManager.update(player);
        PacketSendUtility.sendPacket(player, UpdateTroopResp.valueOf(troop));
    }

    private Troop getTroop(Player player, int type, int index) {
        return player.getTroopStorage().getTroops().stream().filter(t -> t.getType() == type
                && t.getIndex() == index).findFirst().orElse(null);
    }

    /**
     * 修改编队
     *
     * @param player
     * @param type 编队类型
     * @param index 编队索引
     * @param cards 更新后的卡组
     */
    public void alterTroop(Player player, int type, int index, List<Card> cards) {
        if (cards == null) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        Troop troop = getTroop(player, type, index);
        if (troop == null) {
            throw new ManagedException(ManagedErrorCode.TROOP_NOT_EXIST);
        }
        checkCards(player, cards);
        troop.getCards().clear();
        troop.getCards().addAll(cards);
        troopManager.update(player);
        PacketSendUtility.sendPacket(player, UpdateTroopResp.valueOf(troop));
    }

    /**
     * 检查卡组
     *
     * @param player
     * @param cards
     */
    private void checkCards(Player player, List<Card> cards) {
        if (CollectionUtils.isEmpty(cards)) {
            return;
        }
        // 检查卡牌数量
        if (cards.size() > getCardNum(player)) {
            throw new ManagedException(ManagedErrorCode.TROOP_CARD_NUM_LIMIT);
        }
        // 检查卡牌参数
        for (Card card : cards) {
            checkCardParam(player, card);
        }
        // 检查卡牌是否重复
        List<Integer> resourceIds = cards.stream().map(card -> getMwn(player, card).getResourceId())
                .collect(Collectors.toList());
        if (isRepeated(resourceIds)) {
            throw new ManagedException(ManagedErrorCode.TROOP_CARD_REPEATED);
        }
    }

    private void checkCardParam(Player player, Card card) {
        MoWuNiang mwn = getMwn(player, card);
        if (mwn == null) {
            throw new ManagedException(ManagedErrorCode.TROOP_CARD_NOT_EXIST);
        }
        int stage = card.getStage();
        int adjust = card.getAdjust();
        int prob = card.getProb();
        if (stage == 0) {
            card.setAdjust(0);
            card.setProb(0);
            return;
        }
        if ((stage != 1 && stage != 2) || (adjust != 1 && adjust != 2) || (prob != 1 && prob != 2 && prob != 3)) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        int fetterLv = mwn.getFetterLv();
        // 1-10回合阶段,魔物娘羁绊等级9开启
        if (fetterLv < troopStageManager.getResource(stage).getLimitLv()) {
            throw new ManagedException(ManagedErrorCode.MWN_FETTER_LV_LIMIT);
        }
        // 中概率,魔物娘羁绊等级6开启 大概率,魔物娘羁绊等级9开启
        if (fetterLv < troopWeightManager.getResource(prob).getLimitLv()) {
            throw new ManagedException(ManagedErrorCode.MWN_FETTER_LV_LIMIT);
        }
    }

    private MoWuNiang getMwn(Player player, Card card) {
        return player.getPlayerMoWuNiang().get(card.getMwnId());
    }

    private boolean isRepeated(List<Integer> list) {
        if (list.size() == 1) {
            return false;
        }
        Set<Integer> hashSet = new HashSet<>(list);
        return hashSet.size() != list.size();
    }

    /**
     * 编辑编队名字
     *
     * @param player
     * @param type
     * @param index
     * @param name
     */
    public void editTroopName(Player player, int type, int index, String name) {
        Troop troop = getTroop(player, type, index);
        if (troop == null) {
            throw new ManagedException(ManagedErrorCode.TROOP_NOT_EXIST);
        }
        if (troop.getName().equals(name)) {
            throw new ManagedException(ManagedErrorCode.TROOP_NAME_NOT_ALTER);
        }
        // 检查编队名字长度 最多7个中文字符（14个英文字符）
        int length = getLength(name);
        if (length == 0 || length > 14) {
            throw new ManagedException(ManagedErrorCode.TROOP_NAME_LENGTH_LIMIT);
        }
        troop.setName(name);
        troopManager.update(player);
        PacketSendUtility.sendPacket(player, EditTroopNameResp.valueOf(type, index, name));
    }

    private int getLength(String str) {
        try {
            return str.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 编队出战
     *
     * @param player
     * @param type
     * @param index
     */
    public void troopGoFight(Player player, int type, int index) {
        Troop troop = getTroop(player, type, index);
        if (troop == null) {
            throw new ManagedException(ManagedErrorCode.TROOP_NOT_EXIST);
        }
        if (troop.getFight() == TroopGoFight.YES) {
            throw new ManagedException(ManagedErrorCode.TROOP_IS_IN_FIGHT);
        }
        player.getTroopStorage().getTroops().forEach(t -> {
            if (t.getType() == type && t.getFight() == TroopGoFight.YES) {
                t.setFight(TroopGoFight.NO);
            }
        });
        troop.setFight(TroopGoFight.YES);
        troopManager.update(player);
        PacketSendUtility.sendPacket(player, TroopGoFightResp.valueOf(type, index));
    }

    /**
     * 编队切换技能
     *
     * @param player
     * @param type
     * @param index
     * @param skillId
     */
    public void troopChangeSkill(Player player, int type, int index, int skillId) {
        Troop troop = getTroop(player, type, index);
        if (troop == null) {
            throw new ManagedException(ManagedErrorCode.TROOP_NOT_EXIST);
        }
        // TODO 编队技能条件
        if (troop.getSkillId() == skillId) {
            throw new ManagedException(ManagedErrorCode.TROOP_HAS_CHOOSE_SKILL);
        }
        troop.setSkillId(skillId);
        troopManager.update(player);
        PacketSendUtility.sendPacket(player, TroopChangeSkillResp.valueOf(type, index, skillId));
    }

    /**
     * 获取默认解锁编队
     */
    public List<Troop> getUnlockTroops() {
        return troopUnlockManager.getUnlockResources().stream()
                .map(t -> Troop.valueOf(t.getType(), t.getIndex(), t.getName())).collect(Collectors.toList());
    }

    /**
     * 获取出战卡包
     *
     * @param player
     * @param troopType
     * @return
     */
    public CardBag getSelectedCardBag(Player player, int troopType) {
        Troop troop = player.getTroopStorage().getTroops().stream().filter(t -> t.getType() == troopType
                && t.getFight() == TroopGoFight.YES).findFirst().orElse(null);
        if (troop == null) {
            throw new ManagedException(ManagedErrorCode.NO_TROOP_IN_FIGHT);
        }
        List<Card> cards = troop.getCards();
        if (cards.size() != getCardNum(player)) {
            throw new ManagedException(ManagedErrorCode.TROOP_IN_FIGHT_NUM_ERROR);
        }
        List<MoWuNiang> mwns = cards.stream().map(card -> getMwn(player, card)).collect(Collectors.toList());
        return new CardBag(mwns, cards, cards.size(), troop.getSkillId());
    }

    public int getCardNum(Player player) {
        return troopNumManager.getCardNum(player.getPlayerEnt().getLevel());
    }

    /**
     * 设置编队默认卡组
     *
     * @param player
     */
    public void setDefaultCards(Player player) {
        Troop troop = getTroop(player, 1, 1);
        Object[] mwnKeys = ConfigValueManager.getInstance().getArrayConfigValue("TroopCards");
        if (mwnKeys.length != getCardNum(player)) {
            throw new ManagedException(ManagedErrorCode.TROOP_IN_FIGHT_NUM_ERROR);
        }
        List<Card> cards = new ArrayList<>();
        for (Object key : mwnKeys) {
            long mwnKey = Long.parseLong(String.valueOf(key));
            Long mwnId = player.getPlayerMoWuNiang().values().stream().filter(t -> t.getResourceId() == mwnKey)
                    .map(MoWuNiang::getId).findAny().get();
            Card card = Card.valueOf(mwnId, 0, 0, 0);
            cards.add(card);
        }
        troop.setCards(cards);
        troop.setFight(TroopGoFight.YES);
        troopManager.update(player);
    }
}
