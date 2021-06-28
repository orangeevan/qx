package com.mmorpg.qx.module.roundFight.model;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.*;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.roundFight.enums.RoomState;
import com.mmorpg.qx.module.roundFight.enums.RoomType;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.enums.TrainerOnRoomSate;
import com.mmorpg.qx.module.roundFight.packet.BroadcastRoundInfoResp;
import com.mmorpg.qx.module.roundFight.packet.TrainerUseCardNumResp;
import com.mmorpg.qx.module.roundFight.packet.vo.RoomVo;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.worldMap.manager.MapResourceManager;
import com.mmorpg.qx.module.worldMap.model.WorldMap;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author wang ke
 * @description: 玩家匹配完后进入同一个房间，所有业务数据都在这里
 * @since 19:26 2020-08-12
 */
public class Room {
    private static final Logger logger = SysLoggerFactory.getLogger(Room.class);

    /**
     * 魔物娘战斗准备时间，穿戴装备
     */
    public static final int Mwn_Fight_Ready = 5 * 1000;

    /**
     * 房间号唯一
     */
    private int roomId;

    /**
     * 房间名
     */
    private String name;

    /**
     * 房间类型
     */
    private RoomType roomType;

    /**
     * 选择地图ID
     */
    private int worldMapResourceId;

    /**
     * 房间地图
     */
    private WorldMapInstance worldMapInstance;


    /**
     * 当前回合数
     */
    private AtomicInteger round = new AtomicInteger(0);
    /**
     * 单个回合使用技能次数
     */
    private ConcurrentHashMap<Long, Integer> useSkillTimeRound = new ConcurrentHashMap<>();

    /**
     * 当前轮次操作者
     */
    private volatile AbstractTrainerCreature currentTurn;

    /**
     * 当前回合阶段
     */
    private volatile RoundStage roundStage;

    /**
     * 房间成员
     */
    private volatile List<AbstractTrainerCreature> trainers;

    /**
     * 房间创建者
     */
    private AbstractTrainerCreature creator;

    /**
     * 房间先手者，标记新回合
     */
    private AbstractTrainerCreature firstOp;

    /**
     * 房间状态
     */
    private volatile RoomState roomState = RoomState.JOIN;

    /**
     * 房间最大成员
     */
    private volatile int maxMember = 2;

    /**
     * 回合战斗魔物娘双方
     */
    private volatile MWNCreature attacker;
    private volatile MWNCreature defense;

    /**
     * 房间每次魔物娘双方准备后最多等待进入战斗时间
     */
    private volatile long mwnFightReadyTime = 0L;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 房间创建时间点
     */
    private long createTime = 0L;

    /**
     * 房间匹配时间点
     */
    private long matchTime = 0L;

    /**
     * 比赛是否结束
     */
    private AtomicBoolean roundFightOver = new AtomicBoolean(false);

    /**
     * 是否初始化
     */
    private AtomicBoolean init = new AtomicBoolean(false);

    /**
     * 胜利方
     */
    private volatile AbstractTrainerCreature winner;

    /**
     * 房间初始化时间点
     */
    private long roomInitTime = 0L;

    /**
     * 记录魔物娘死亡次数
     */
    private ConcurrentHashMap<Long, Integer> mwnDeadTimes = new ConcurrentHashMap<>();

    /**
     * 房间计时器
     */
    private StopWatch stopWatch;

    public Room(int roomId, RoomType roomType, String roomName) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.name = roomName;
        this.createTime = System.currentTimeMillis();
        stopWatch = new StopWatch();
    }


    /**
     * 房间初始化过程
     */
    public void initRoom() {
        /** 初始化地图，驯养师进入地图*/
        WorldMap worldMap = MapResourceManager.getInstance().getWorldMap(this.worldMapResourceId);
        if (worldMap == null) {
            throw new RuntimeException("地图不存在：" + this.worldMapResourceId);
        }
        WorldMapInstance worldMapInstance = WorldMapService.getInstance().createWorldMapInstance(worldMap, true);
        this.setWorldMapInstance(worldMapInstance);

        /** 玩家进入地图*/
        if (!CollectionUtils.isEmpty(trainers)) {
            /** 打乱次序,随机顺序*/
            Collections.shuffle(trainers);
            /** 设置首席操作者*/
            firstOp = trainers.get(0);
            trainers.forEach(trainer -> {
                /** 设置出生点*/
                trainer.setPosition(getBirthPosition());
                /** 设置出生方向*/
                trainer.setDir(worldMap.getBirthDir());
                trainer.initAttr();
                RoundFightService.getInstance().initFirstRoundMp(trainer);
                WorldMapService.getInstance().spawn(trainer);
                if (trainer instanceof RobotTrainerCreature) {
                    trainer.setOnRoomSate(TrainerOnRoomSate.Ready_Fight);
                }
            });
        }

        /** 每个驯养师给三张牌*/
        trainers.forEach(trainer -> {
            List<MoWuNiang> packItems = RoundFightService.getInstance().randomCard(trainer.getSourceCardStorage(), 3);
            if (!CollectionUtils.isEmpty(packItems)) {
                packItems.forEach(packetItem -> trainer.getUseCardStorage().addMwn(packetItem));
            }
        });
        currentTurn = trainers.get(0);
        roundStage = RoundStage.Wait_Trainer_Ready;
        roomInitTime = System.currentTimeMillis();
    }

    public void syncRoomInitInfo(AbstractTrainerCreature creature) {
        if (RelationshipUtils.isPlayerTrainer(creature)) {
            PacketSendUtility.sendPacket(creature, ((PlayerTrainerCreature) creature).getInfo());
        }
        /**地图所有生物进入驯养师视野*/
        Iterator<AbstractVisibleObject> objectIterator = worldMapInstance.objectIterator();
        while (objectIterator.hasNext()) {
            AbstractVisibleObject next = objectIterator.next();
            if (next != creature) {
                creature.getController().see(next);
            }
        }
        PacketSendUtility.sendPacket(creature, TrainerUseCardNumResp.valueOf(creature));
        RoundFightUtils.trainerCardsChange(creature, creature.getUseCardStorage().getMwns(), true, false, Reason.Round_Extract_Card);
        if (getRound() > 0) {
            BroadcastRoundInfoResp roundInfoResp = BroadcastRoundInfoResp.valueOf(getRound(), getRoundStage(), getCurrentTurn().getObjectId());
            PacketSendUtility.sendPacket(creature, roundInfoResp);
        }
    }

    public AbstractTrainerCreature getCreator() {
        return creator;
    }

    public void setCreator(AbstractTrainerCreature creator) {
        this.creator = creator;
        if (trainers == null) {
            trainers = new ArrayList<>();
        }
        trainers.add(creator);
    }

    public AbstractTrainerCreature getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(AbstractTrainerCreature currentTurn) {
        this.currentTurn = currentTurn;
    }

    public RoundStage getRoundStage() {
        return roundStage;
    }

    public synchronized void setRoundStage(RoundStage roundStage) {
        this.roundStage = roundStage;
    }

    public AbstractTrainerCreature getNextTrainer() {
        lock.readLock().lock();
        try {
            int index = trainers.indexOf(currentTurn);
            if (index == trainers.size() - 1) {
                return trainers.get(0);
            }
            ++index;
            return trainers.get(index);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getRound() {
        return round.get();
    }

    public void increaseRound() {
        round.incrementAndGet();
    }

    public void clearUseSkillTimeRound() {
        useSkillTimeRound.clear();
    }

    public void addUseSkillTimeRound(long key) {
        useSkillTimeRound.compute(key, (k, v) ->
                (v == null ? 0 : v) + 1
        );
    }

    public int getUseSkillTimeRound(long key) {
        return useSkillTimeRound.getOrDefault(key, 0);
    }

    public int getRoomId() {
        return roomId;
    }

    /***
     *  房间成员副本,如果要修改成员成员列表，调用对应业务方法
     * @return
     */
    public List<AbstractTrainerCreature> getTrainers() {
        return new ArrayList<>(trainers);
    }

    public void setTrainers(List<AbstractTrainerCreature> trainers) {
        this.trainers = trainers;
    }

    public AtomicBoolean getRoundFightOver() {
        return roundFightOver;
    }

    public AtomicBoolean getInit() {
        return init;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWorldMapResourceId() {
        return worldMapResourceId;
    }

    public void setWorldMapResourceId(int worldMapResourceId) {
        this.worldMapResourceId = worldMapResourceId;
    }

    public WorldMapInstance getWorldMapInstance() {
        return worldMapInstance;
    }

    public void setWorldMapInstance(WorldMapInstance worldMapInstance) {
        this.worldMapInstance = worldMapInstance;
    }

    public Room(int roomId, int worldMapResourceId) {
        this.roomId = roomId;
        this.worldMapResourceId = worldMapResourceId;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }

    public synchronized void matchSuccess() {
        roomState = RoomState.MATCH_SUCCESS;
        this.matchTime = System.currentTimeMillis();
    }

    public RoomVo toRoomVo() {
        RoomVo roomVo = new RoomVo();
        roomVo.setRoomId(this.roomId);
        roomVo.setCreaterId(this.creator.getObjectId());
        roomVo.setCreaterName(this.creator.getName());
        roomVo.setRoomType(roomType);
        roomVo.setMaxMember(maxMember);
        roomVo.setRoomState(roomState);
        roomVo.setCurrentMember(trainers == null ? 0 : trainers.size());
        roomVo.setMapId(getWorldMapResourceId());
        return roomVo;
    }

    public boolean isBeforeStart() {
        return isOnState(RoomState.JOIN) || isOnState(RoomState.MATCH_SUCCESS);
    }

    public boolean isStart() {
        return isOnState(RoomState.START);
    }

    public boolean isEnd() {
        return isOnState(RoomState.END);
    }

    /***
     * 房间正式开始,该方法只在roomManger start方法调用，确保线程安全
     */
    public void onStart() {
        roomState = RoomState.START;
        stopWatch.start();
    }

    public boolean addTrainer(AbstractTrainerCreature trainer) {
        lock.writeLock().lock();
        try {
            if (isFull()) {
                return false;
            }
            trainers.add(trainer);
            if (getTrainers().size() == getMaxMember()) {
                matchSuccess();
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeTrainer(AbstractTrainerCreature trainer) {
        lock.writeLock().lock();
        try {
            trainers.remove(trainer);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /***
     * 房间消息统一线程
     * @return
     */
    public int getDispatcherHashCode() {
        return getRoomId();
    }

    public AbstractTrainerCreature getFirstOp() {
        return firstOp;
    }

    public WorldPosition getBirthPosition() {
        if (Objects.isNull(this.worldMapInstance)) {
            return null;
        }
        return this.worldMapInstance.getBirthPosition();
    }

    /**
     * 设置回合战斗双方魔物娘
     *
     * @param fightA
     * @param fightB
     */
    public void setRoundFightMWN(MWNCreature fightA, MWNCreature fightB) {
        lock.writeLock().lock();
        try {
            this.attacker = fightA;
            this.defense = fightB;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 回合战斗结束，清除魔物娘
     */
    public void clearRoundFightMWN() {
        lock.writeLock().lock();
        try {
            this.attacker.resetFightReady();
            this.defense.resetFightReady();
            this.attacker = null;
            this.defense = null;
            mwnFightReadyTime = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /***
     * 房间心跳处理，所有需要定时执行或周期执行业务，都可以在这里处理
     */
    public void heartbeat() {
        try {
            //地图心跳
            worldMapInstance.heartBeat(this);
            mwnRoundFight();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private void mwnRoundFight() {
        //战斗双方都准备好战斗或战斗截止时间到开启战斗
        if ((Objects.nonNull(attacker) && Objects.nonNull(defense)) && ((attacker.isReadyFight() && defense.isReadyFight()) || (this.mwnFightReadyTime > 0 && System.currentTimeMillis() >= this.mwnFightReadyTime))) {
            try {
                RoundFightUtils.fight(attacker, defense);
                //有魔物娘战斗房间超时时长延长，等待客户端
                //this.getCurrentTurn().setRsTimeOut(System.currentTimeMillis() + 60000);
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                clearRoundFightMWN();
            }
        }
    }

    /***
     * 驯养师开启新回合触发一些业务逻辑,该方法只能在room线程调用
     */
    public void startNewRound(AbstractTrainerCreature trainerCreature) {
        try {
            /** 处理地图所有对象buff*/
            if (Objects.isNull(trainerCreature)) {
                return;
            }
            /**先执行驯养师buff处理*/
            try {
                trainerCreature.handleEffect(TriggerType.ROUND_BEGIN, trainerCreature);
            } catch (Exception e) {
                logger.error("", e);
            }
            Collection<MWNCreature> mwnCreatures = trainerCreature.getMWN(true);
            if (!CollectionUtils.isEmpty(mwnCreatures)) {
                /** 执行魔物娘buff处理*/
                mwnCreatures.forEach(mwn -> {
                    try {
                        mwn.handleEffect(TriggerType.ROUND_BEGIN, mwn);
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                });
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }


    public boolean isAttackMWN(MWNCreature attacker) {
        return this.attacker == attacker;
    }

    public boolean isDefenseMWN(MWNCreature defense) {
        return this.defense == defense;
    }


    public long getMwnFightReadyTime() {
        return mwnFightReadyTime;
    }

    public void setMwnFightReadyTime(long mwnFightReadyTime) {
        lock.writeLock().lock();
        try {
            this.mwnFightReadyTime = mwnFightReadyTime;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public MWNCreature getEnemy(MWNCreature mwn) {
        if (attacker == mwn) {
            return defense;
        }
        if (defense == mwn) {
            return attacker;
        }
        return null;
    }

    public boolean isInStage(RoundStage... stage) {
        return Arrays.stream(stage).anyMatch(sta -> sta == roundStage);
    }

    public AbstractTrainerCreature getWinner() {
        return winner;
    }

    public void setWinner(AbstractTrainerCreature winner) {
        lock.writeLock().lock();
        try {
            this.winner = winner;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isOnState(RoomState state) {
        return roomState == state;
    }


    public void onEnd() {
        lock.writeLock().lock();
        try {
            if (this.isOnState(RoomState.START)) {
                stopWatch.stop();
            }
            if (!this.isEnd()) {
                roomState = RoomState.END;
                trainers.stream().forEach(trainerCreature -> trainerCreature.getController().onExitRoom());
                this.destroy();
            }
        } catch (Exception e) {
            logger.error("房间结束异常", e);
        } finally {
            lock.writeLock().unlock();
        }
    }


    public long getRoomInitTime() {
        return roomInitTime;
    }

    public boolean isFull() {
        return trainers.size() >= getMaxMember();
    }

    public ReadWriteLock getRoomLock() {
        return lock;
    }

    /**
     * 计数玩家开启战斗，全部玩家开启战斗才真正启动房间
     */
    public boolean isEveryOnRoomState(TrainerOnRoomSate onRoomState) {
        if (CollectionUtils.isEmpty(trainers)) {
            return false;
        }
        for (AbstractTrainerCreature trainerCreature : trainers) {
            if (!trainerCreature.isOnRoomState(onRoomState)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 房间内存在某个玩家
     *
     * @param player
     * @return
     */
    public boolean hasPlayer(Player player) {
        if (getPlayerTrainerCreature(player) != null) {
            return true;
        }
        return false;
    }

    public PlayerTrainerCreature getPlayerTrainerCreature(Player player) {
        Optional<PlayerTrainerCreature> result = getTrainers().stream().filter(trainer -> trainer instanceof PlayerTrainerCreature)
                .map(trainer -> (PlayerTrainerCreature) trainer).filter(playerTrainer -> playerTrainer.isOwner(player)).findFirst();
        return result.orElse(null);
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getMatchTime() {
        return matchTime;
    }

    /**
     * 记录魔物娘死亡次数
     *
     * @param mwnCreature
     */
    public void recordMwnDead(MWNCreature mwnCreature) {
        if (mwnDeadTimes.containsKey(mwnCreature.getMwn().getId())) {
            mwnDeadTimes.put(mwnCreature.getMwn().getId(), mwnDeadTimes.get(mwnCreature.getMwn().getId()) + 1);
        } else {
            mwnDeadTimes.put(mwnCreature.getMwn().getId(), 1);
        }
    }

    public int getMwnDeadTimes(MoWuNiang mwn) {
        if (mwnDeadTimes.containsKey(mwn.getId())) {
            return mwnDeadTimes.get(mwn.getId());
        }
        return 0;
    }

    private void destroy() {
        this.mwnDeadTimes.clear();
        this.trainers.clear();
        this.creator = null;
        this.currentTurn = null;
        this.firstOp = null;
        this.winner = null;
        if (Objects.nonNull(this.worldMapInstance)) {
            this.worldMapInstance.onDestroy();
            this.setWorldMapInstance(null);
        }
    }

    /**
     * 房间运行时间
     *
     * @return
     */
    public int getRunTime() {
        return (int) (stopWatch.getTime() / 1000);
    }
}
