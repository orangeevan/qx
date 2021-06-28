package com.mmorpg.qx.module.object.gameobject;

import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.item.model.NewEquipmentStorage;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.AbstractCreatureController;
import com.mmorpg.qx.module.object.controllers.AbstractTrainerController;
import com.mmorpg.qx.module.object.controllers.stats.CreatureLifeStats;
import com.mmorpg.qx.module.object.gameobject.attr.*;
import com.mmorpg.qx.module.roundFight.enums.ActStage;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.enums.TrainerOnRoomSate;
import com.mmorpg.qx.module.roundFight.model.DicePoint;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.troop.model.CardBag;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 驯养师
 * @since 19:59 2020-08-12
 */
public abstract class AbstractTrainerCreature extends AbstractCreature {

    /**
     * 当前回合中使用卡牌
     */
    private CardBag useCardStorage;

    /**
     * 装备栏
     */
    private NewEquipmentStorage equipmentStorage;

    /**
     * 进入战场前准备卡组,这个卡组从头到尾都不会参与游戏过程
     */
    private CardBag sourceCardStorage;

    /**
     * 玩家当前房间
     */
    private Room room;

    /**
     * 当前骰子点数
     */
    private DicePoint dicePoint;

    /**
     * 当前阶段超时时间点
     */
    private volatile long rsTimeOut;

    /**
     * 召唤魔物娘
     */
    private Map<Long, MWNCreature> summons = new ConcurrentHashMap<>();

    /**
     * 当前出战魔物娘
     */
    private MWNCreature fightMWN;

    /**
     * 本回合是否指定地格召唤魔物娘战斗
     */
    private Set<Integer> callMwnFight = new HashSet<>();

    /**
     * 当前行为阶段,当一个行为结束时，重置
     */
    private AtomicReference<ActStage> actStage = new AtomicReference<>(null);

    /**
     * 队伍id
     */
    private int teamId;

    /**
     * 默认卡组最大长度
     */
    private int maxStorageSize = 7;

    /**
     * 本回合是否某种特殊情况下已经召唤过魔物娘
     */
    private volatile boolean isCallMwnRound = false;

    /**
     * 设置驯养师处于房间状态
     */
    private volatile TrainerOnRoomSate onRoomSate;

    /**
     * 抽卡是卡牌为0次数，掉血随次数叠加
     */
    private AtomicInteger extractCardZero = new AtomicInteger(0);

    private List<Integer> callMwnGrids = new ArrayList<>(2);

    /**
     * 法强
     */
    private float magicStrength = 1;

    /**
     * 卡组战力总值
     */
    private int cardsTotalForce = 0;

    /**
     * 是否是替换魔物娘出生
     */
    private boolean isReplaceSpawn = false;
    /**
     * 一个回合内战斗次数
     */
    private int fightRScount;


    public AbstractTrainerCreature(long objId, AbstractCreatureController<? extends AbstractCreature> controller, WorldPosition position) {
        super(objId, controller, position);
        equipmentStorage = new NewEquipmentStorage(ItemManager.getInstance().getCardBagsMaxCardNum());
    }

    /**
     * 超时
     */
    public boolean isRSTimeOut() {
        return rsTimeOut < System.currentTimeMillis();
    }

    public synchronized void setRsTimeOut(long rsTimeOut) {
        if (this.isObjectType(ObjectType.ROBOT_TRAINER)) {
            if (!this.getRoom().isInStage(RoundStage.MOVE, RoundStage.MOVE_END)) {
                this.rsTimeOut = System.currentTimeMillis() + 800;
            } else {
                this.rsTimeOut = System.currentTimeMillis() + 5000;
            }
            return;
        }
        this.rsTimeOut = rsTimeOut;
    }

    public boolean syncActStage(ActStage expectStage, ActStage newStage) {
        return actStage.compareAndSet(expectStage, newStage);
    }

    public boolean isInActStage(ActStage expectStage) {
        return actStage.get() == expectStage;
    }

    public void resetActStage() {
        actStage.set(null);
    }

    @Override
    public int getDispatcherHashCode() {
        return this.getRoom().getDispatcherHashCode();
    }

    public void initAttr() {
        //初始化驯养师属性
        this.setAttrController(new CreatureAttrController(this));
        List<Attr> initAttrList = new ArrayList<>(getInitAttrList());
        this.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Level_Base), initAttrList, true, false);
        this.setLifeStats(new CreatureLifeStats(this, this.getAttrController().getCurrentAttr(AttrType.Max_Hp), this.getAttrController().getCurrentAttr(AttrType.Max_Mp)));
        initMagicStrength();
    }

    public abstract List<Attr> getInitAttrList();

    public Collection<MWNCreature> getMWN(boolean isAlive) {
        if (CollectionUtils.isEmpty(summons)) {
            return Collections.emptyList();
        }
        if (isAlive) {
            return summons.values().stream().filter(MWNCreature::isAlive).collect(Collectors.toList());
        } else {
            return summons.values().stream().filter(MWNCreature::isAlreadyDead).filter(creature -> creature.getMwn() != null).collect(Collectors.toList());
        }
    }

    public void addMWN(MWNCreature mwn) {
        if (Objects.isNull(mwn)) {
            return;
        }
        List<MWNCreature> oldMwnCreatures = summons.values().stream().filter(mwnCreature -> mwnCreature.getMwn().equals(mwn.getMwn())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(oldMwnCreatures)) {
            oldMwnCreatures.forEach(oldMwnCreature -> summons.remove(oldMwnCreature.getObjectId()));
        }
        summons.put(mwn.getObjectId(), mwn);
    }

    public int mwgAvgLv() {
        if (useCardStorage.getCurrentSize() == 0) {
            return 0;
        }
        return Math.floorDiv(useCardStorage.getMwns().stream().mapToInt(MoWuNiang::getLevel).sum(), useCardStorage.getCurrentSize());
    }

    /**
     * 计算魔法强度
     * 魔法强度=（队伍总等级÷卡牌数*0.02+队伍总星级÷卡牌数*0.2）；该值最高为2
     * 受魔法强度影响的effect效果，计算方式为：效果*魔法强度。
     *
     * @return
     */
    public void initMagicStrength() {
        List<MoWuNiang> mwns = sourceCardStorage.getMwns();
        if (CollectionUtils.isEmpty(mwns)) {
            return;
        }
        int size = mwns.size();
        int totalLv = 0;
        int totalStar = 0;
        for (MoWuNiang mwn : mwns) {
            totalLv += mwn.getLevel();
            //TODO 星级
        }
        float result = totalLv * 0.02f / size + totalStar * 0.2f / size;
        magicStrength = result > 2 ? 2 : result;
    }

    /**
     * 返回卡组 战力值=总生命值/卡组总费（向下取整）。
     *
     * @return
     */
    public int initCardTotalForce() {
        List<MoWuNiang> mwns = sourceCardStorage.getMwns();
        if (CollectionUtils.isEmpty(mwns)) {
            return 0;
        }
        int hpTotal = mwns.stream().mapToInt(mwn -> mwn.getResource().getMax_hp()).sum();
        int costTotal = mwns.stream().mapToInt(mwn -> mwn.getResource().getCostMp()).sum();
        return Math.floorDiv(hpTotal, costTotal);
    }


    @Override
    public AbstractTrainerController<? extends AbstractTrainerCreature> getController() {
        return (AbstractTrainerController) super.getController();
    }

    public boolean isSetMwnStatus() {
        return this.getEffectController().isInStatus(EffectStatus.Set_Mwn);
    }

    public boolean hasMwn(long id) {
        return summons.containsKey(id);
    }

    public MWNCreature getMwn(long id) {
        return summons.get(id);
    }

    public MWNCreature getMwnCreatureByCardId(long cardId) {
        return summons.values().stream().filter(mwn -> mwn.getMwn().getId() == cardId).findFirst().orElse(null);
    }

    public void removeMwnCreature(long id) {
        summons.remove(id);
    }

    /**
     * 设置开启战斗状态
     */
    public void startRoomFight() {
        setOnRoomSate(TrainerOnRoomSate.Start_Room_Fight);
    }


    public void setOnRoomSate(TrainerOnRoomSate onRoomSate) {
        this.onRoomSate = onRoomSate;
    }

    public boolean isOnRoomState(TrainerOnRoomSate onRoomSate) {
        return this.onRoomSate == onRoomSate;
    }

    public int incExtractCardZero(int num) {
        return extractCardZero.addAndGet(num);
    }

    public void addCallMwnGrids(Collection<Integer> grids) {
        callMwnGrids.addAll(grids);
    }

    public void resetCallMwnGrids() {
        callMwnGrids.clear();
    }

    public List<Integer> getCallMwnGrids() {
        return callMwnGrids;
    }

    public boolean isCallMwnFight(int gridId) {
        return callMwnFight.contains(gridId);
    }

    public void resetCallMwnFight() {
        callMwnFight.clear();
    }

    public void setCallMwnFight(int gridId) {
        callMwnFight.add(gridId);
    }

    public Collection<MWNCreature> getAllMwn() {
        return summons.values();
    }

    public boolean isSelfRoundTurn() {
        return getRoom().getCurrentTurn() == this;
    }

    public boolean isReplaceSpawn() {
        return isReplaceSpawn;
    }

    public void setReplaceSpawn(boolean replaceSpawn) {
        isReplaceSpawn = replaceSpawn;
    }

    public CardBag getUseCardStorage() {
        return useCardStorage;
    }

    public void setUseCardStorage(CardBag useCardStorage) {
        this.useCardStorage = useCardStorage;
    }

    public CardBag getSourceCardStorage() {
        return sourceCardStorage;
    }

    public void setSourceCardStorage(CardBag sourceCardStorage) {
        this.sourceCardStorage = sourceCardStorage;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public DicePoint getDicePoint() {
        return dicePoint;
    }

    public void setDicePoint(DicePoint dicePoint) {
        this.dicePoint = dicePoint;
    }

    public void resetDicePoint() {
        this.dicePoint = null;
    }

    public MWNCreature getFightMWN() {
        return fightMWN;
    }

    public void setFightMWN(MWNCreature fightMWN) {
        this.fightMWN = fightMWN;
    }

    public void clearFightMWN() {
        this.fightMWN = null;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getMaxStorageSize() {
        return maxStorageSize;
    }

    public void setMaxStorageSize(int maxStorageSize) {
        this.maxStorageSize = maxStorageSize;
    }

    public NewEquipmentStorage getEquipmentStorage() {
        return equipmentStorage;
    }

    public synchronized void callMwn() {
        isCallMwnRound = true;
    }

    public boolean isCallMwnRound() {
        return isCallMwnRound;
    }

    public synchronized void resetCallMwnRound() {
        isCallMwnRound = false;
    }

    public float getMagicStrength() {
        return magicStrength;
    }

    /**
     * 退出战斗清除对象，加快GC
     */
    public void clearAllMwn() {
        summons.clear();
    }

    public int getCardsTotalForce() {
        return cardsTotalForce;
    }

    public void setCardsTotalForce(CardBag cardBag) {
        // 战力值=卡组总生命值/卡组总费
        List<MoWuNiang> mwns = cardBag.getMwns();
        int totalHp = 0;
        for (MoWuNiang mwn : mwns) {
            int hp = mwn.getAttrValue(AttrType.Max_Hp);
            totalHp += hp;
        }
        int totalCostMp = 0;
        for (MoWuNiang mwn : mwns) {
            int mp = mwn.getResource().getCostMp();
            totalCostMp += mp;
        }
        this.cardsTotalForce = totalHp / totalCostMp;
    }

    public int clearFightRScount() {
        return fightRScount = 0;
    }

    public void addFightRScount() {
        this.fightRScount += 1;
    }

    public int getFightRScount() {
        return fightRScount;
    }

}
