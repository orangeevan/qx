package com.mmorpg.qx.module.roundFight.manager;

import com.haipaite.common.event.core.EventBusManager;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.threadpool.IdentityEventExecutorGroup;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.roundFight.enums.*;
import com.mmorpg.qx.module.roundFight.event.RoundStageChangeEvent;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActSelector;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description:房间管理者
 * @since 19:29 2020-08-12
 */
@Component
public class RoomManager implements ApplicationListener<ContextStartedEvent> {

    private Logger logger = SysLoggerFactory.getLogger(RoomManager.class);

    /**
     * 房间id生成器
     */
    private AtomicInteger roomIdGen = new AtomicInteger(0);

    /**
     * 进入正常战斗房间
     */
    private ConcurrentHashMap<Room, Future> allRooms = new ConcurrentHashMap<>(100);

    /**
     * 缓存房间队列,后续可根据服务器房间数控制负载
     */
    private ConcurrentHashMap<Integer, Room> roomCache = new ConcurrentHashMap<>();

    /**
     * 进入房间战斗后最大准备战斗时长
     */
    private final int ROOM_START_MAX_WAIT = 60000;

    /**
     * 房间最大存活时间，防止特殊情况房间无法结束
     */
    private final int ROOM_MAX_EXIST_TIME = 3600;

    /**
     * 房间最大匹配时间
     */
    private final int ROOM_MATCH_MAX_TIME = 60 * 1000;

    /**
     * 匹配成功后，玩家开启战斗最大准备时长，没有确认战斗视为放弃，房间会到点被清除
     */
    private final int ROOM_START_MAX_WAIT_TIME = 60 * 1000;

    private AtomicBoolean startCheck = new AtomicBoolean(false);

    private void startCheck() {
        IdentityEventExecutorGroup.addScheduleAtFixedRate(new AbstractDispatcherHashCodeRunable() {
            @Override
            public int getDispatcherHashCode() {
                return 1;
            }

            @Override
            public String name() {
                return "RoomManager";
            }

            @Override
            public void doRun() {
                checkMatchOverdue();
                roomStatus();
            }
        }, 10, 5, TimeUnit.SECONDS);
    }

    /***
     * 创建空房间
     * @return
     */
    public Room createRoom(RoomType type, String roomName) {
        Room room = new Room(roomIdGen.incrementAndGet(), type, roomName);
        roomCache.put(room.getRoomId(), room);
        return room;
    }

    /***
     * 房间创建完后，启动一个回合战斗。类似链式反应，每个阶段异常都处理掉，防止整个流程瘫痪
     * @param room
     */
    public void startRoomRoundFight(Room room) {
        try {
            room.getRoomLock().writeLock().lock();
            /** 检查所有成员是否开启战斗*/
            if (!room.isEveryOnRoomState(TrainerOnRoomSate.Start_Room_Fight)) {
                return;
            }
            /** 房间已经开启战斗无须再次开启 */
            if (room.isStart()) {
                return;
            }
            //设置房间开启
            room.onStart();
            /** 房间初始化*/
            if (room.getInit().compareAndSet(false, true)) {
                try {
                    room.initRoom();
                    // 驯养师开局先后手顺序
                    RoundFightService.getInstance().trainerOrder(room);
                } catch (Exception e) {
                    logger.error("房间初始化异常", e);
                    //初始化异常直接结束
                    logger.error(String.format("当前房间 [%d] 初始化异常回合结束", room.getRoomId()));
                    allRooms.remove(room);
                    room.onEnd();
                    roomCache.remove(room.getRoomId());
                    throw e;
                }
            }
            ScheduledFuture<?> scheduledFuture = IdentityEventExecutorGroup.addScheduleAtFixedRate(new AbstractDispatcherHashCodeRunable() {
                @Override
                public int getDispatcherHashCode() {
                    return room.getRoomId();
                }

                @Override
                public String name() {
                    return room.toString();
                }

                @Override
                public void doRun() {
                    executeRoomRoundStage(room);
                }
            }, 1000, 100, TimeUnit.MILLISECONDS);
            allRooms.put(room, scheduledFuture);
            roomCache.remove(room.getRoomId());
        } finally {
            room.getRoomLock().writeLock().unlock();
        }
    }

    public List<Room> getAllJoinRoom() {
        return new ArrayList<>(roomCache.values());
    }

    public List<Room> getRooms(RoomType type, RoomState state) {
        if (state == RoomState.JOIN) {
            return roomCache.values().stream().filter(room -> room.getRoomType() == type && room.isOnState(state)).collect(Collectors.toList());
        }
        return allRooms.keySet().stream().filter(room -> room.getRoomType() == type && room.isOnState(state)).collect(Collectors.toList());
    }

    public Room getJoinRoom(int roomId) {
        if (roomCache.containsKey(roomId)) {
            return roomCache.get(roomId);
        }
        return null;
    }

    /**
     * 根据玩家获取房间
     *
     * @param player
     * @return
     */
    public Room getPlayerRoom(Player player) {
        //先从匹配房间查找
        for (Room room : getAllJoinRoom()) {
            if (room.hasPlayer(player)) {
                return room;
            }
        }
        //其次从战斗房间查找
        for (Room room : allRooms.keySet()) {
            if (room.hasPlayer(player)) {
                return room;
            }
        }
        return null;
    }

    /**
     * 玩家是否处于房间
     *
     * @param player
     * @return
     */
    public boolean isPlayerInRoom(Player player) {
        return Objects.nonNull(getPlayerRoom(player));
    }

    public void removeRoom(Room room) {
        Future remove = allRooms.remove(room);
        if (Objects.nonNull(remove)) {
            remove.cancel(true);
        }
        roomCache.remove(room.getRoomId());
    }

    /**
     * 检查匹配过期房间
     */
    public void checkMatchOverdue() {
        if (CollectionUtils.isEmpty(roomCache)) {
            return;
        }
        for (Room room : roomCache.values()) {
            try {
                //匹配超时
                if (room.isOnState(RoomState.JOIN) && room.getCreateTime() + ROOM_MATCH_MAX_TIME < System.currentTimeMillis()) {
                    roomCache.remove(room.getRoomId());
                    room.onEnd();
                    logger.info(String.format("移除房间【%s】, 匹配超时", room.getRoomId()));
                    return;
                }
                //匹配成功，有玩家未确认开启战斗超时检查
                if (room.isOnState(RoomState.MATCH_SUCCESS) && room.getMatchTime() + ROOM_START_MAX_WAIT_TIME < System.currentTimeMillis()) {
                    roomCache.remove(room.getRoomId());
                    room.onEnd();
                    logger.info(String.format("移除房间【%s】, 匹配成功，等待进入战斗超时", room.getRoomId()));
                    return;
                }
            } catch (Exception e) {
                logger.error("移除超时房间", e);
            }
        }
    }

    private void roomStatus() {
        if (!allRooms.isEmpty()) {
            StringBuilder roomInfo = new StringBuilder();
            roomInfo.append("全服当前房间数 【 ").append(allRooms.size()).append(" 】");
            logger.info(roomInfo.toString());
        }
    }

    /**
     * 房间流程推演
     *
     * @param room
     */
    private void executeRoomRoundStage(Room room) {
        try {
            //房间等待玩家进入战斗状态，不阻塞线程
            if (room.getRoundStage() == RoundStage.Wait_Trainer_Ready) {
                //房间所有成员没有全部进入准备状态，房间准备状态时间未过，继续等待
                if (!room.isEveryOnRoomState(TrainerOnRoomSate.Ready_Fight) && room.getRoomInitTime() + ROOM_START_MAX_WAIT >= System.currentTimeMillis()) {
                    return;
                }
                switchNextRoundStage(room);
            }

            //轮回数处理 第一位操作者第一阶段作为一个新回合标志
            if (room.getRoundStage() == RoundStage.Extract_Card_Before && room.getCurrentTurn().isInActStage(null)) {
                if (room.getCurrentTurn() == room.getFirstOp()) {
                    room.increaseRound();
                    room.clearUseSkillTimeRound();
                    room.getWorldMapInstance().findMWN().stream().forEach(mwn -> mwn.handleEffect(TriggerType.ROUND, mwn));
                    room.getWorldMapInstance().findMWN().stream().forEach(mwn -> mwn.handleEffect(TriggerType.ROUND, mwn));
                }
                //开启新回合
                room.startNewRound(room.getCurrentTurn());
                String format = String.format("当前房间 [%s] 开启新回合，第 [%s] 回合，当前操作者类型 [%s] id 【%d】 血量 : [%s]  魔法 : [%s]  玩家【%s】", room.getRoomId(), room.getRound(),
                        room.getCurrentTurn().getObjectType().name(), room.getCurrentTurn().getObjectId(),
                        room.getCurrentTurn().getLifeStats().getCurrentHp(), room.getCurrentTurn().getLifeStats().getCurrentMp(),
                        room.getCurrentTurn().getName());
                logger.info(format);
            }
            // 房间心跳，房间内包括技能buff效果等
            room.heartbeat();
            // 当前驯养师进入准备阶段
            if (room.getCurrentTurn().syncActStage(null, ActStage.READY)) {
                try {
                    RoundFightService.getInstance().getActByObjectType(room.getRoundStage(), room.getCurrentTurn().getObjectType()).ready(room.getCurrentTurn());
                    /***设置超时时间*/
                    room.getCurrentTurn().setRsTimeOut(System.currentTimeMillis() + room.getRoundStage().getRSStartTimeOut());
                    String format = String.format("当前房间 [%s] 第 [%s] 回合 [%s]阶段开启，当前操作者类型 [%s], ID [%d] 玩家【%s】", room.getRoomId(), room.getRound(), room.getRoundStage(),
                            room.getCurrentTurn().getObjectType().name(), room.getCurrentTurn().getObjectId(), room.getCurrentTurn().getName());
                    logger.info(format);
                } catch (Exception e) {
                    logger.error("回合准备阶段异常", e);
                } finally {
                    room.getCurrentTurn().syncActStage(ActStage.READY, ActStage.START);
                    /**广播回合新步骤*/
                    RoundFightService.getInstance().broadRoundStageInfo(room);
                }
            }
            /**检查玩家驯养师超时,超时代走完流程*/
            if (room.getCurrentTurn().isInActStage(ActStage.START)) {
                AbstractRSActSelector actSelector = RoundFightService.getInstance().getActByObjectType(room.getRoundStage(), room.getCurrentTurn().getObjectType());
                if (room.getCurrentTurn().isRSTimeOut()) {
                    try {
                        actSelector.start(room.getCurrentTurn());
                    } catch (Exception e) {
                        logger.error("回合开始阶段异常", e);
                    } finally {
                        if (room.getCurrentTurn().syncActStage(ActStage.START, ActStage.END)) {
                            room.getCurrentTurn().setRsTimeOut(System.currentTimeMillis() + room.getRoundStage().getRSEndTimeOut());
                        }
                    }
                }
            }
            if (room.getCurrentTurn().isInActStage(ActStage.END)) {
                try {
                    AbstractRSActSelector actSelector = RoundFightService.getInstance().getActByObjectType(room.getRoundStage(), room.getCurrentTurn().getObjectType());
                    if (room.getCurrentTurn().isRSTimeOut()) {
                        String format = String.format("当前房间 [%s] 第 [%s] 回合 [%s]阶段结束[%d]，当前操作者类型 [%s], ID [%d]", room.getRoomId(), room.getRound(), room.getRoundStage(), System.currentTimeMillis(),
                                room.getCurrentTurn().getObjectType().name(), room.getCurrentTurn().getObjectId());
                        logger.info(format);
                        actSelector.end(room.getCurrentTurn());
                    }
                } catch (Exception e) {
                    logger.error("回合结束阶段异常", e);
                }
            }
            /**检查是否有驯养师死亡*/
            processTrainerDead(room);
        } catch (Exception e) {
            logger.error("房间异常", e);
        } finally {
            /**房间结束检查处理*/
            processRoomOnEnd(room);
        }
    }

    /**
     * 检查驯养师状态，结束设置状态
     *
     * @param room
     */
    private void processTrainerDead(Room room) {
        //血量为0结束
        AbstractTrainerCreature anyDead = room.getTrainers().stream().filter(AbstractCreature::isAlreadyDead).findAny().orElse(null);
        if (Objects.isNull(anyDead)) {
            return;
        }
        if (room.getRoundFightOver().compareAndSet(false, true)) {
            String format = String.format("当前房间 [%d] 第 [%d] 回合，驯养师 [%s] [%d] 血量为0回合结束", room.getRoomId(), room.getRound(), anyDead.getObjectType().name(), anyDead.getObjectId());
            logger.info(format);
            room.setWinner(room.getTrainers().stream().filter(AbstractCreature::isAlive).findAny().get());
        }
    }


    /**
     * 房间进入结束状态，处理相应业务
     *
     * @param room
     */
    private void processRoomOnEnd(Room room) {
        if (!room.getRoundFightOver().get() && room.getRunTime() < ROOM_MAX_EXIST_TIME) {
            return;
        }
        try {
            room.onEnd();
            String format = String.format("房间 【%s】 回合战斗结束,所花时间【%s】 秒", room.getRoomId(), room.getRunTime());
            System.out.println(format);
            logger.info(format);
        } catch (Exception e) {
            logger.error("房间结尾清算异常", e);
        } finally {
            Future remove = allRooms.remove(room);
            if (Objects.nonNull(remove)) {
                remove.cancel(true);
            }
        }
    }

    /**
     * 房间切换阶段
     * 进入下一阶段
     *
     * @param room
     */
    public void switchNextRoundStage(Room room) {
        switchRoundStage(room, room.getRoundStage().getNextStage());
    }

    /**
     * 切换房间进入指定阶段
     *
     * @param room
     * @param roundStage
     */
    public void switchRoundStage(Room room, RoundStage roundStage) {
        room.setRoundStage(roundStage);
        EventBusManager.getInstance().syncSubmit(RoundStageChangeEvent.valueOf(room));
    }


    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
        if (startCheck.compareAndSet(false, true)) {
            logger.info("房间匹配过期检查开启");
            startCheck();
        }
    }

    public static RoomManager getInstance() {
        return BeanService.getBean(RoomManager.class);
    }
}
