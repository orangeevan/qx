package com.mmorpg.qx.module.roundFight.service;

import com.google.common.collect.Lists;
import com.haipaite.common.event.core.EventBusManager;
import com.haipaite.common.utility.RandomUtils;
import com.haipaite.common.utility.SelectRandom;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.configValue.ConfigValueManager;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.equipment.enums.EquipUseLimit;
import com.mmorpg.qx.module.equipment.model.EquipItem;
import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.mwn.service.MWNService;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.controllers.PlayerTrainerController;
import com.mmorpg.qx.module.object.controllers.RobotTrainerController;
import com.mmorpg.qx.module.object.creater.MWNCreater;
import com.mmorpg.qx.module.object.gameobject.*;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.player.manager.PlayerCommonManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.player.model.PlayerCommonType;
import com.mmorpg.qx.module.roundFight.enums.*;
import com.mmorpg.qx.module.roundFight.event.MwnFightBeforeEvent;
import com.mmorpg.qx.module.roundFight.event.PlayerCallMwnEvent;
import com.mmorpg.qx.module.roundFight.event.PlayerMwnReplaceEvent;
import com.mmorpg.qx.module.roundFight.manager.RoomManager;
import com.mmorpg.qx.module.roundFight.manager.RoundStageActManager;
import com.mmorpg.qx.module.roundFight.model.DicePoint;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActSelector;
import com.mmorpg.qx.module.roundFight.packet.*;
import com.mmorpg.qx.module.roundFight.packet.vo.RoomVo;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.model.effect.EffectType;
import com.mmorpg.qx.module.skill.model.effect.impl.MoveAddTrainerHarm;
import com.mmorpg.qx.module.skill.model.skillResult.DamageSkillResult;
import com.mmorpg.qx.module.skill.packet.SkillBuildCdResp;
import com.mmorpg.qx.module.trainer.manager.TrainerManager;
import com.mmorpg.qx.module.trainer.module.PlayerTrainer;
import com.mmorpg.qx.module.trainer.resource.AITrainerResource;
import com.mmorpg.qx.module.troop.model.CardBag;
import com.mmorpg.qx.module.troop.service.TroopService;
import com.mmorpg.qx.module.worldMap.enums.DirType;
import com.mmorpg.qx.module.worldMap.manager.MapResourceManager;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wang ke
 * @description:
 * @since 19:04 2020-08-13
 */
@Component
public class RoundFightService {

    private Logger logger = SysLoggerFactory.getLogger(RoundFightService.class);

    @Autowired
    private RoomManager roomManager;

    public static RoundFightService getInstance() {
        return BeanService.getBean(RoundFightService.class);
    }

    /***
     * 从卡包随机指定数量卡牌，随机完后，从卡包移除
     * @param cardBag
     * @param num
     * @return
     */
    public List<MoWuNiang> randomCard(CardBag cardBag, int num) {
        if (CollectionUtils.isEmpty(cardBag.getMwns()) || num < 1) {
            return null;
        }
        int size = cardBag.getCurrentSize();
        if (size < num) {
            num = size;
        }
        SelectRandom<MoWuNiang> selectRandom = new SelectRandom<>();
        cardBag.getMwns().forEach(moWuNiang -> selectRandom.addElement(moWuNiang, 1));
        List<MoWuNiang> result = new ArrayList<>(num);
        List<MoWuNiang> randomRs = selectRandom.run(num);
        randomRs.forEach(mwn -> {
            cardBag.removeMwn(mwn);
            result.add(mwn);
        });
        return result;
    }

    /**
     * 按照权重从卡包随机指定数量卡牌，随机完后，从卡包移除
     *
     * @param cardBag
     * @param roundNum
     * @param num
     * @return
     */
    public List<MoWuNiang> randomCard(CardBag cardBag, int roundNum, int num) {
        if (CollectionUtils.isEmpty(cardBag.getMwns()) || num < 1) {
            return null;
        }
        int size = cardBag.getCurrentSize();
        if (size < num) {
            num = size;
        }
        SelectRandom<MoWuNiang> selectRandom = new SelectRandom<>();
        cardBag.getCards().forEach(card -> {
            MoWuNiang moWuNiang = cardBag.getById(card.getMwnId());
            int weight = card.getWeight(roundNum);
            selectRandom.addElement(moWuNiang, weight);
        });
        List<MoWuNiang> result = new ArrayList<>(num);
        List<MoWuNiang> randomRs = selectRandom.run(num);
        randomRs.forEach(mwn -> {
            cardBag.removeMwn(mwn);
            result.add(mwn);
        });
        return result;
    }

    public void roundAddCard(AbstractTrainerCreature trainer, int num, Reason reason) {
        List<MoWuNiang> packItems = randomCard(trainer.getSourceCardStorage(), trainer.getRoom().getRound(), num);
        StringBuilder packItemsBuilder = new StringBuilder("[");
        if (!CollectionUtils.isEmpty(packItems)) {
            packItems.forEach(moWuNiang -> packItemsBuilder.append(moWuNiang.getId()).append(","));
        }
        packItemsBuilder.append("]");
        String format = String.format("%s 开始抽卡 抽卡原因：%s ,数量: %s, 抽到卡牌数量: %s 卡牌id: %s", trainer.getObjectId(), reason, num, packItems == null ? 0 : packItems.size(), packItemsBuilder.toString());
        logger.info(format);
        if (!CollectionUtils.isEmpty(packItems)) {
            RoundFightUtils.trainerCardsChange(trainer, packItems, false, true, reason);
        }
        //卡包没牌直接扣血
        if (CollectionUtils.isEmpty(packItems) || packItems.size() < num) {
            int deduct = num;
            if (!CollectionUtils.isEmpty(packItems)) {
                deduct -= packItems.size();
            }
            while (deduct > 0 && !trainer.isAlreadyDead()) {
                int reduceHp = (int) Math.pow(2, trainer.incExtractCardZero(1));
                trainer.getLifeStats().reduceHp(reduceHp, trainer, Reason.No_Card, true);
                logger.info(String.format("【%s】 没牌，扣血【%s】", trainer.getMaster().getObjectId(), reduceHp));
                deduct--;
                sendBlackCard(trainer);
            }
        } else {
            List<MoWuNiang> gainCards = new ArrayList<>();
            List<MoWuNiang> explodeCards = new ArrayList<>();
            for (MoWuNiang mwn : packItems) {
                if (!trainer.getUseCardStorage().addMwn(mwn)) {
                    //移除的魔物娘进入驯养师召唤列表，以待复活
                    MWNCreature mwnCreature = MWNCreater.create(trainer, mwn, null);
                    mwnCreature.getLifeStats().reduceHp(mwnCreature.getCurrentHp(), null, 0, 0);
                    trainer.addMWN(mwnCreature);
                    explodeCards.add(mwn);
                    continue;
                }
                StringBuilder builder = new StringBuilder("[");
                trainer.getUseCardStorage().getMwns().forEach(moWuNiang -> builder.append(moWuNiang.getId() + ","));
                builder.append("]");
                String info = "驯养师: " + trainer.getObjectId() + "  获得魔物娘: " + mwn.getId() + " 配置id:" + mwn.getResourceId() + " 当前魔物娘：" + builder;
                logger.info(info);
                gainCards.add(mwn);
            }
            if (!CollectionUtils.isEmpty(gainCards)) {
                RoundFightUtils.trainerCardsChange(trainer, gainCards, true, false, reason);
            }
            if (!CollectionUtils.isEmpty(explodeCards)) {
                RoundFightUtils.trainerCardsChange(trainer, explodeCards, false, false, Reason.Explode_Card);
            }
        }
    }

    /**
     * 发黑牌
     *
     * @param trainer
     */
    public void sendBlackCard(AbstractCreature trainer) {
        MoWuNiang fakeMwn = new MoWuNiang();
        fakeMwn.setId(-1);
        fakeMwn.setResourceId(-1);
        //fakeMwn.setAttrList(new ArrayList<>());
        ArrayList<MoWuNiang> list = new ArrayList<>();
        list.add(fakeMwn);
        //RoundFightUtils.trainerCardsChange(trainer, list, false, true, reason);
        PacketSendUtility.broadcastInWorldMap(trainer, RoundCardUpdateResp.valueOf(trainer.getObjectId(), list, false, true, Reason.No_Card), true);
    }

    /***
     * 随机指定数量骰子
     * @param num
     * @return
     */
    public int[] randomDice(int num, int[] poionts) {
        int[] result = new int[num];
        while (num > 0) {
            int index = RandomUtils.betweenInt(0, poionts.length - 1, true);
            result[--num] = poionts[index];
        }
        return result;
    }

    /**
     * 返回不同骰子点数
     *
     * @param num
     * @return
     */
    public int[] randomDiffDice(int num) {
        if (num > 6) {
            throw new IllegalArgumentException("随机骰子数量不能超过6");
        }
        int[] randomIndexes = RandomUtils.getRandomArray(1, 6, num);
        randomIndexes = Arrays.stream(randomIndexes).distinct().toArray();
        if (randomIndexes.length < num) {
            for (int i = 0; i < 6; i++) {
                if (!ArrayUtils.contains(randomIndexes, i)) {
                    ArrayUtils.add(randomIndexes, i);
                    if (randomIndexes.length == num) {
                        break;
                    }
                }
            }
        }
        randomIndexes = Arrays.stream(randomIndexes).sorted().toArray();
        return ArrayUtils.subarray(randomIndexes, 0, num);
    }


    /**
     * 获取指定生物，指定阶段行为选择器
     *
     * @param stage
     * @param objectType
     * @return
     */
    public AbstractRSActSelector getActByObjectType(RoundStage stage, ObjectType objectType) {
        AbstractRSActSelector rsActAutoSelector = RoundStageActManager.getInstance().getRSActAutoSelector(stage, objectType);
        if (Objects.isNull(rsActAutoSelector)) {
            throw new IllegalArgumentException(String.format("不支持 回合阶段 【%s】 对象类型 【%s】", stage, objectType));
        }
        return rsActAutoSelector;
    }


    /**
     * 创建房间
     * //TODO Demo版本由于没有设计匹配机制，直接创建房间战斗
     *
     * @param player
     */
    public void createRoom(Player player, PlayerCreateRoomReq req) {
        logger.info("收到创建房间请求: " + req.getTrainerId());
        if (player.getTrainerCreature() != null || roomManager.isPlayerInRoom(player)) {
            throw new ManagedException(ManagedErrorCode.PLAYER_IN_ROOM);
        }
        PlayerTrainer trainer = player.getTrainerCreature(req.getTrainerId());
        //TODO 测试代码
        if (trainer == null) {
            trainer = player.getPlayerTrainers().get(0);
        }
        CardBag cardBag = TroopService.getInstance().getSelectedCardBag(player, req.getTroopType());
        player.setSelectedCardBag(cardBag);
        player.setSelectedTrainer(trainer);

        //TODO demo阶段再创建房间前检查是否已有同类房间，有直接加入房间准备战斗
        List<Room> allRoom = roomManager.getRooms(req.getRoomType(), RoomState.JOIN);
        Room room;
        if (CollectionUtils.isEmpty(allRoom)) {
            PlayerTrainerCreature trainerCreature = new PlayerTrainerCreature
                    (trainer.getTrainerId(), new PlayerTrainerController(), null, trainer, cardBag.getSkillId());
            player.setTrainerCreature(trainerCreature);
            trainerCreature.setOwner(player);
            trainerCreature.setSourceCardStorage(cardBag.deepCopy());
            trainerCreature.setCardsTotalForce(cardBag);
            trainerCreature.setUseCardStorage(new CardBag(ItemManager.getInstance().getRoundMaxCardNum()));
            trainerCreature.getUseCardStorage().setOwner(trainerCreature);
            room = roomManager.createRoom(req.getRoomType(), req.getName());
            room.setRoomType(req.getRoomType());
            room.setWorldMapResourceId(req.getMapId());
            room.setCreator(trainerCreature);
            trainerCreature.setRoom(room);
            if (req.getRoomType() == RoomType.PVE) {
                if (room.isOnState(RoomState.JOIN)) {
                    /** PVE 加入机器人*/
                    room.matchSuccess();
                    RoundFightService.getInstance().joinRobotTrainer(room);
                }
                //roundFightStart(trainerCreature);
                //return;
            }
        } else {//TODO 前期测试
            //加入房间
            boolean joinSuccess = false;
            for (Room r : allRoom) {
                try {
                    joinRoom(trainer, r.getRoomId(), req.getTroopType(), req.getSkillId());
                    joinSuccess = true;
                    break;
                } catch (Exception e) {
                    logger.error("", e);
                    throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
                }
            }
            //所有房间没有一个能正常加入，创建房间
            if (!joinSuccess) {
                createRoom(player, req);
            }
            return;
        }
        RoomVo roomVo = room.toRoomVo();
        UpdateRoomStateResp roomInfoResp = UpdateRoomStateResp.valueOf(roomVo);
        PacketSendUtility.sendPacket(player, roomInfoResp);
    }

    /**
     * 开启战斗
     *
     * @param trainerCreature
     */
    public void roundFightStart(AbstractTrainerCreature trainerCreature) {
        Room room = trainerCreature.getRoom();
        if (room == null) {
            throw new ManagedException(ManagedErrorCode.ROOM_NOT_EXISTS);
        }

        if (room.isOnState(RoomState.JOIN) || room.isOnState(RoomState.END)) {
            throw new ManagedException(ManagedErrorCode.ROOM_STATE_ERROR);
        }
        trainerCreature.startRoomFight();
        //如果房间已经开启，同步房间信息
        if (room.isStart()) {
            room.syncRoomInitInfo(trainerCreature);
            return;
        }
        roomManager.startRoomRoundFight(room);
        //room.syncRoomInitInfo(trainerCreature);
    }

    /**
     * 请求系统房间信息
     *
     * @param player
     */
    public void roomInfos(Player player) {
        List<Room> allRooms = roomManager.getAllJoinRoom();
        if (CollectionUtils.isEmpty(allRooms)) {
            return;
        }
        List<RoomVo> voList = allRooms.stream().map(Room::toRoomVo).collect(Collectors.toList());
        RoomsInfoResp roomInfoResp = RoomsInfoResp.valueOf(voList);
        PacketSendUtility.sendPacket(player, roomInfoResp);
    }

    /**
     * 加入房间
     *
     * @param trainer
     * @param roomId
     * @param troopType
     */
    public void joinRoom(PlayerTrainer trainer, int roomId, int troopType, int skillId) {
        if (trainer.getOwner().getTrainerCreature() != null) {
            throw new ManagedException(ManagedErrorCode.PLAYER_IN_ROOM);
        }
        Room room = roomManager.getJoinRoom(roomId);
        if (room == null || room.getRoomType() == RoomType.PVE) {
            throw new ManagedException(ManagedErrorCode.ROOM_NOT_EXISTS);
        }
        if (!room.isOnState(RoomState.JOIN)) {
            throw new ManagedException(ManagedErrorCode.ROOM_IN_FIGHT);
        }
        if (room.getTrainers().size() >= room.getMaxMember()) {
            throw new ManagedException(ManagedErrorCode.ROOM_FULL);
        }
        CardBag cardBag = TroopService.getInstance().getSelectedCardBag(trainer.getOwner(), troopType);
        PlayerTrainerCreature trainerCreature = new PlayerTrainerCreature
                (trainer.getTrainerId(), new PlayerTrainerController(), null, trainer, cardBag.getSkillId());
        //加入成功设置房间驯养师
        if (!room.addTrainer(trainerCreature)) {
            throw new ManagedException(ManagedErrorCode.ROOM_FULL);
        }
        trainer.getOwner().setSelectedCardBag(cardBag);
        trainerCreature.setSourceCardStorage(cardBag.deepCopy());
        trainerCreature.setCardsTotalForce(cardBag);
        trainerCreature.setUseCardStorage(new CardBag(ItemManager.getInstance().getRoundMaxCardNum()));
        trainerCreature.getUseCardStorage().setOwner(trainerCreature);
        trainer.getOwner().setTrainerCreature(trainerCreature);
        trainerCreature.setOwner(trainer.getOwner());
        trainerCreature.setRoom(room);
        /**加入成功更新房间信息*/
        UpdateRoomStateResp updateRoomStateResp = UpdateRoomStateResp.valueOf(room.toRoomVo());
        room.getTrainers().forEach(tr -> PacketSendUtility.sendPacket(tr, updateRoomStateResp));
        return;
    }


    /**
     * 玩家请求查询房间信息
     *
     * @param player
     * @param roomId
     */
    public Room searchRoom(Player player, int roomId) {
        Room room = roomManager.getJoinRoom(roomId);
        if (Objects.isNull(room)) {
            throw new ManagedException(ManagedErrorCode.ROOM_NOT_EXISTS);
        }
        return room;
    }

    /**
     * 广播回合新阶段
     *
     * @param room
     */
    public void broadRoundStageInfo(Room room) {
        //System.err.println("当前回合操作者: " + room.getCurrentTurn().getObjectId() + " 房间号:" + room.getRoomId());
        BroadcastRoundInfoResp broadcastPacket = BroadcastRoundInfoResp.valueOf(room.getRound(), room.getRoundStage(), room.getCurrentTurn().getObjectId());
        PacketSendUtility.broadcastInWorldMap(room.getCurrentTurn(), broadcastPacket, true);
    }

    public void joinRobotTrainer(Room room) {
        if (room == null) {
            throw new ManagedException(ManagedErrorCode.ROOM_NOT_EXISTS);
        }
        if (room.getTrainers().size() >= room.getMaxMember()) {
            throw new ManagedException(ManagedErrorCode.ROOM_FULL);
        }
        //while (room.getTrainers().size() < room.getMaxMember()) {
        long robotTrainderId = IdentifyManager.getInstance().getNextIdentify(IdentifyManager.IdentifyType.MAP_CREATURE);
        //TODO 机器人随机法则后续补充
        AITrainerResource trainerResource = TrainerManager.getInstance().getAITrainerResource(1);
        RobotTrainerCreature robotTrainer = new RobotTrainerCreature(robotTrainderId, new RobotTrainerController(), room.getBirthPosition(), trainerResource);
        room.addTrainer(robotTrainer);
        robotTrainer.setRoom(room);
        /**加入成功更新房间信息*/
        //UpdateRoomStateResp updateRoomStateResp = UpdateRoomStateResp.valueOf(room.toRoomVo());
        //room.getTrainers().forEach(tr -> PacketSendUtility.sendPacket(tr, updateRoomStateResp));
        robotTrainer.startRoomFight();
        //}
    }

    /**
     * 召唤魔物娘
     *
     * @param trainer
     * @param mwnId
     * @param gridId
     */
    public void callMWN(AbstractTrainerCreature trainer, long mwnId, int gridId) {
        //驯养师是否在战斗场景
        if (trainer == null || !trainer.isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        if (trainer.getEffectController().isInStatus(EffectStatus.Limit_Call_Mwn)) {
            throw new ManagedException(ManagedErrorCode.CALL_MWN_LIMIT_EFFECT);
        }
        if (!trainer.isSelfRoundTurn()) {
            throw new ManagedException(ManagedErrorCode.NOT_TURN_OPERATOR);
        }
        //使用者卡包检查
        if (Objects.isNull(trainer.getUseCardStorage()) || !trainer.getUseCardStorage().hasMwn(mwnId)) {
            StringBuilder builder = new StringBuilder("[");
            trainer.getUseCardStorage().getMwns().forEach(mwn -> builder.append(mwn.getId()).append(","));
            builder.append("]");
            String logInfo = "请求招呼魔物娘： " + mwnId + " 不存在手牌里，手牌： " + builder + " 驯养师id: " + trainer.getObjectId();
            logger.error(logInfo);
            throw new ManagedException(ManagedErrorCode.MWN_NOT_EXISTS);
        }
        //驯养师状态检查
        AbstractRSActSelector actByRoundStage = getActByObjectType(trainer.getRoom().getRoundStage(), ObjectType.PLAYER_TRAINER);
        if (!actByRoundStage.isActionAccept(PacketId.PLAYER_CALL_SUMMON_REQ) && !trainer.isSetMwnStatus()) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        /** 不是驯养师格子*/
        if (gridId > 0 && trainer.getGridId() != gridId) {
            if (!trainer.getEffectController().isInStatus(EffectStatus.Set_Mwn) && !trainer.getEffectController().isInStatus(EffectStatus.Mwn_Assault)) {
                System.err.println(String.format("驯养师【%s】 当前格子【%s】,请求召唤魔物娘格子【%s】", trainer.getObjectId(), trainer.getGridId(), gridId));
                throw new ManagedException(ManagedErrorCode.GRID_ERROR);
            }
            if (trainer.getWorldMapInstance().hasMwnInGrid(gridId)) {
                throw new ManagedException(ManagedErrorCode.GRID_ERROR);
            }
            if (trainer.getWorldMapInstance().getPosition(gridId) == null) {
                throw new ManagedException(ManagedErrorCode.GRID_ERROR);
            }
            List<Integer> callMwnGrids = trainer.getCallMwnGrids();
            if (!CollectionUtils.isEmpty(callMwnGrids) && !callMwnGrids.contains(gridId)) {
                throw new ManagedException(ManagedErrorCode.GRID_ERROR);
            }
            if (Objects.nonNull(trainer.getWorldMapInstance().getMWNByGridId(gridId))) {
                throw new ManagedException(ManagedErrorCode.GRID_ERROR);
            }

//            List<MapGrid> aroundGrid = GameUtil.findAroundGrid(trainer.getWorldMapInstance().getParent(), trainer.getGridId(), false);
//            if (CollectionUtils.isEmpty(aroundGrid)) {
//                throw new ManagedException(ManagedErrorCode.GRID_ERROR);
//            }
//            //驯养师两侧空格子
//            if (trainer.getEffectController().isInStatus(EffectStatus.Arround_Blank_Grid_Set_Mwn)) {
//                final int gid = gridId;
//                if (!aroundGrid.stream().filter(mapGrid -> mapGrid.getId() == gid).findAny().isPresent()) {
//                    throw new ManagedException(ManagedErrorCode.GRID_ERROR);
//                }
//            } else {//强袭触发魔物娘两侧
//                List<MWNCreature> mwnCreatures = null;
//                Collection<MWNCreature> aliveMwns = trainer.getMWN(true);
//                if (!CollectionUtils.isEmpty(aliveMwns)) {
//                    mwnCreatures = aliveMwns.stream().filter(mwn -> mwn.getEffectController().isInStatus(EffectStatus.Mwn_Assault)).collect(Collectors.toList());
//                }
//                if (CollectionUtils.isEmpty(mwnCreatures)) {
//                    throw new ManagedException(ManagedErrorCode.GRID_ERROR);
//                }
//                boolean checkGrids = false;
//                for (MWNCreature mwnCreature : mwnCreatures) {
//                    List<MapGrid> grids = GameUtil.findAroundGrid(trainer.getWorldMapInstance().getParent(), mwnCreature.getGridId(), false);
//                    for (MapGrid g : grids) {
//                        if (g.getId() == gridId) {
//                            checkGrids = true;
//                            break;
//                        }
//                    }
//                    if (!checkGrids) {
//                        throw new ManagedException(ManagedErrorCode.GRID_ERROR);
//                    }
//                    if (Objects.nonNull(trainer.getWorldMapInstance().getMWNByGridId(gridId))) {
//                        throw new ManagedException(ManagedErrorCode.GRID_ERROR);
//                    }

        } else {
            gridId = trainer.getGridId();
        }
        //检查格子能否放魔物娘
        MapGrid mapGrid = MapResourceManager.getInstance().getWorldMap(trainer.getMapId()).getMapGrid(gridId);
        if (mapGrid == null || !mapGrid.canSetMwn()) {
            throw new ManagedException(ManagedErrorCode.CALL_MWN_LIMIT_BUILD);
        }
        if (trainer.getWorldMapInstance().findBuildByGid(gridId) != null) {
            throw new ManagedException(ManagedErrorCode.CALL_MWN_LIMIT_BUILD);
        }
        MWNCreature mwnInGrid = trainer.getWorldMapInstance().getMWNByGridId(gridId);
        //瘫痪不可替换下场
        if (Objects.nonNull(mwnInGrid)) {
            if (RelationshipUtils.judgeRelationship(trainer, mwnInGrid, RelationshipUtils.Relationships.SELF_TRAINER_MWN)) {
                if (mwnInGrid.getEffectController().isInStatus(EffectStatus.Paralysis)) {
                    return;
                }
            }
        }
//        if (Objects.nonNull(mwnInGrid) && RelationshipUtils.judgeRelationship(trainer, mwnInGrid, RelationshipUtils.Relationships.SELF_TRAINER_MWN)) {
//            throw new ManagedException(ManagedErrorCode.HAS_SELF_MWN);
//        }
        //投骰子前目标格子有敌方魔物娘不能召唤
        if (Objects.nonNull(mwnInGrid) && RelationshipUtils.judgeRelationship(trainer, mwnInGrid, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN) && trainer.getRoom().isInStage(RoundStage.Throw_Dice_Before)) {
            throw new ManagedException(ManagedErrorCode.HAS_ENEMY_MWN);
        }

        if (trainer.getEffectController().isInStatus(EffectStatus.Set_Mwn)) {
            trainer.getEffectController().unsetStatus(EffectStatus.Set_Mwn, false);
            trainer.callMwn();
        }
        MoWuNiang mwn = trainer.getUseCardStorage().getById(mwnId);
        int index = trainer.getUseCardStorage().getMwns().indexOf(mwn);
        boolean isExile = false;
        if (index == trainer.getUseCardStorage().getCurrentSize() - 1) {
            isExile = true;
        }
        MWNResource mwnResource = mwn.getResource();
        int costMp = mwnResource.getCostMp();
        int mwnDeadTimes = trainer.getRoom().getMwnDeadTimes(mwn);
        if (mwnDeadTimes > 0) {
            costMp -= mwnDeadTimes;
        }
        costMp = costMp < 0 ? 0 : costMp;
        //魔法判断
        if (trainer.getLifeStats().getCurrentMp() < costMp) {
            throw new ManagedException(ManagedErrorCode.CALL_MWN_LIMIT_MP, mwnResource.getName());
        }
        trainer.getLifeStats().reduceMp(costMp, Reason.Call_Mwn, false, true);
        trainer.addConsumeMpRS(costMp);
        //扣除魔物娘
        trainer.getUseCardStorage().reduceMwn(mwnId);
        StringBuilder builder = new StringBuilder("[");
        trainer.getUseCardStorage().getMwns().forEach(moWuNiang -> builder.append(moWuNiang.getId()).append(","));
        builder.append("]");
        String logInfo = "请求招呼魔物娘： " + mwnId + " 配置id: " + mwnResource.getId() + mwnResource.getName() + " 手牌剩余： " + builder + " 驯养师id: " + trainer.getObjectId();
        logger.info(logInfo);
        RoundFightUtils.trainerCardsChange(trainer, Stream.of(mwn).collect(Collectors.toList()), false, false, Reason.Call_Mwn);

        /** 召唤*/
        WorldPosition position = trainer.getWorldMapInstance().getPosition(gridId);
        if (position == null) {
            position = WorldMapService.getInstance().createWorldPosition(trainer.getWorldMapInstance(), gridId);
        }
        MWNCreature mwnCreature = MWNCreater.create(trainer, mwn, position);
        mwnCreature.setMaster(trainer);
        //魔物娘流放添加特殊属性
        if (isExile) {
            mwnCreature.setExile(true);
            //mwnCreature.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Mwn_Exile), Attr.valueOf(AttrType.Trainer_Harm, 5), true);
        }

        /** 魔物娘天赋技能效果处理*/
        if (!trainer.getWorldMapInstance().hasMwnInGrid(mwnCreature.getGridId())) {
            trainer.addMWN(mwnCreature);
            WorldMapService.getInstance().spawn(mwnCreature);
        } else {
            //如果召唤格已有己方魔物娘，则替换，敌方则进入魔物娘战斗
            if (RelationshipUtils.judgeRelationship(trainer, mwnInGrid, RelationshipUtils.Relationships.SELF_TRAINER_MWN)) {
                //先设置魔物娘死亡
                mwnInGrid.getLifeStats().reduceHp(mwnInGrid.getCurrentHp(), trainer, Reason.Mwn_Replace, false);
                //把魔物娘从格子上移除
                mwnInGrid.getController().delete();
                trainer.addMWN(mwnCreature);
                //新魔物娘出生
                WorldMapService.getInstance().spawn(mwnCreature);
                //召唤魔物娘
                PlayerMwnReplaceEvent event = PlayerMwnReplaceEvent.valueOf(mwnCreature, mwnInGrid);
                EventBusManager.getInstance().syncSubmit(event);
            } else {
                //如果驯养师在本回合已经发生过战斗
                if (trainer.isCallMwnFight(gridId)) {
                    throw new ManagedException(ManagedErrorCode.SAME_GRID_ONCE_MWN_FIGHT);
                }
                /**设置出战魔物娘准备战斗,通知前端*/
                trainer.addMWN(mwnCreature);
                trainer.setFightMWN(mwnCreature);
                MWNCreature enemy = trainer.getWorldMapInstance().getMWNByGridId(mwnCreature.getGridId());
                enemy.getMaster().setFightMWN(enemy);
                //先把魔物娘加到地图上，战斗目标选择必须在地图上
                WorldMapService.getInstance().spawn(mwnCreature);
                NotifyMWNFightReadyResp fightResp = NotifyMWNFightReadyResp.valueOf(mwnCreature, enemy);
                //全地图广播准备魔物娘战斗
                PacketSendUtility.broadcastInWorldMap(trainer, fightResp, true);
                trainer.getRoom().setRoundFightMWN(mwnCreature, enemy);
                trainer.getRoom().setMwnFightReadyTime(System.currentTimeMillis() + Room.Mwn_Fight_Ready);
            }
        }
        //召唤魔物娘
        PlayerCallMwnEvent event = PlayerCallMwnEvent.valueOf(mwnCreature, index);
        EventBusManager.getInstance().syncSubmit(event);
    }

    /**
     * 魔物娘穿戴装备后，属性变化，战斗结束后，属性移除
     *
     * @param trainer
     * @param mwnId
     * @param equipId
     */
    public void mwnWearEquip(AbstractTrainerCreature trainer, long mwnId, long equipId) {
        //驯养师是否在战斗场景
        if (!trainer.isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        if (trainer.getEffectController().isInStatus(EffectStatus.Limit_Use_Equip)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        //魔物娘检查
        if (!trainer.hasMwn(mwnId)) {
            throw new ManagedException(ManagedErrorCode.MWN_NOT_EXISTS);
        }
        //装备类型检查
        EquipItem equipItem = trainer.getEquipmentStorage().getEquipById(equipId);
        if (Objects.isNull(equipItem)) {
            throw new ManagedException(ManagedErrorCode.MO_EQUIPS);
        }
        //装备使用次数耐久度检查
        if (equipItem.getUseTimes() >= equipItem.getDurability()) {
            throw new ManagedException(ManagedErrorCode.ITEM_USE_LIMIT);
        }
        MWNCreature mwnCreature = trainer.getMwn(mwnId);
        //驯养师状态检查
        if (trainer.getRoom().getCurrentTurn() == trainer) {
            AbstractRSActSelector actByRoundStage = getActByObjectType(trainer.getRoom().getRoundStage(), ObjectType.PLAYER_TRAINER);
            if (!actByRoundStage.isActionAccept(PacketId.MWN_WEAR_EQUIP_REQ)) {
                throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
            }
            //进攻方不能穿戴防守方装备
            if (equipItem.getResource().getEquipUseLimit() == EquipUseLimit.Use_Defend) {
                throw new ManagedException(ManagedErrorCode.EQUIP_WEAR_ERROR);
            }
        } else {//被动参与者检查魔物id
            if (!trainer.getRoom().isDefenseMWN(mwnCreature)) {
                throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
            }
            //防守方不能穿进攻装备
            if (equipItem.getResource().getEquipUseLimit() == EquipUseLimit.Use_Attack) {
                throw new ManagedException(ManagedErrorCode.EQUIP_WEAR_ERROR);
            }
        }
        mwnCreature.wearEquip(equipItem);
        EquipmentUpdateResp updateResp = EquipmentUpdateResp.valueOf(trainer, equipItem, false);
        PacketSendUtility.sendPacket(trainer, updateResp);
        //mwnCreature.fightReady();
    }

    /**
     * 请求开启魔物娘战斗
     *
     * @param trainer
     * @param mwnId
     */
    public void mwnFight(AbstractTrainerCreature trainer, long mwnId) {
        //驯养师是否在战斗场景
        if (!trainer.isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }

        MWNCreature moWuNiangCreature = trainer.getMwn(mwnId);
        if (!trainer.getRoom().isAttackMWN(moWuNiangCreature) && !trainer.getRoom().isDefenseMWN(moWuNiangCreature)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        MWNCreature enemy = trainer.getRoom().getEnemy(moWuNiangCreature);
        if (!moWuNiangCreature.isReadyFight() || !enemy.isReadyFight()) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        Room room = trainer.getRoom();
        //驯养师状态检查
        if (trainer.getRoom().getCurrentTurn() == trainer) {
            AbstractRSActSelector actByRoundStage = getActByObjectType(room.getRoundStage(), ObjectType.PLAYER_TRAINER);
            if (!actByRoundStage.isActionAccept(PacketId.MWN_FIGHT_REQ)) {
                throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
            }
        } else {//被动参与者检查魔物id
            if (!room.isDefenseMWN(moWuNiangCreature)) {
                throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
            }
        }
        //对战前，
        EventBusManager.getInstance().syncSubmit(MwnFightBeforeEvent.valueOf(moWuNiangCreature, enemy));
        RoundFightUtils.fight(moWuNiangCreature, enemy);
        trainer.addFightRScount();
        room.clearRoundFightMWN();
    }

    /**
     * 驯养师移动
     *
     * @param trainer
     * @param req
     */
    public void trainerMove(AbstractTrainerCreature trainer, TrainerMoveReq req) {
        logger.info("收到客户端移动请求: " + trainer.getObjectId() + " 房间号:" + trainer.getRoom().getRoomId() + " 当前阶段：" + trainer.getRoom().getRoundStage() + " 当前格子:" + trainer.getGridId());
        //驯养师是否在战斗场景
        if (!trainer.isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        if (!trainer.isSelfRoundTurn()) {
            throw new ManagedException(ManagedErrorCode.NOT_TURN_OPERATOR);
        }
        //驯养师状态检查
        AbstractRSActSelector actByRoundStage = getActByObjectType(trainer.getRoom().getRoundStage(), ObjectType.PLAYER_TRAINER);
        if (!actByRoundStage.isActionAccept(PacketId.TRAINER_MOVE_REQ)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        //判断是否移动完成
        DicePoint dicePoint = trainer.getDicePoint();
        if (dicePoint == null || dicePoint.getRound() != trainer.getRoom().getRound()) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        if (dicePoint.calcPoints() <= trainer.getMoveController().getStep()) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }

        if (req.getCurrGrid() != trainer.getPosition().getGridId()) {
            trainer.sendUpdatePosition();
        }
        int step = dicePoint.calcPoints() - trainer.getMoveController().getStep();
        // DirType dir = req.getDir() == null ? trainer.getDir() : req.getDir();
        DirType dir = trainer.getDir();
        String logInfo = "驯养师 " + trainer.getObjectId() + " 请求移动，驯养师自己方向:" + trainer.getDir().name() + " 请求方向：" + req.getDir().name() + " 当前格子:" + trainer.getGridId() + " 投骰子点数：" + dicePoint.calcPoints();
        System.err.println(logInfo);
        logger.info(logInfo);
        trainer.getMoveController().startMove(step, dir, true);

    }


    /**
     * 魔物娘准备战斗
     *
     * @param trainer
     * @param req
     */
    public void mwnFightReady(PlayerTrainerCreature trainer, MWNFightReady req) {
        //驯养师是否在战斗场景
        if (!trainer.isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        //魔物娘检查
        Collection<MWNCreature> mwnCreatures = trainer.getMWN(true);
        if (mwnCreatures == null) {
            throw new ManagedException(ManagedErrorCode.MWN_NOT_EXISTS);
        }
        Optional<MWNCreature> first = mwnCreatures.stream().filter(mwn -> mwn.getObjectId() == req.getMwnId()).findFirst();
        if (!first.isPresent()) {
            throw new ManagedException(ManagedErrorCode.MWN_NOT_EXISTS);
        }
        MWNCreature moWuNiangCreature = first.get();
        //驯养师状态检查
        if (!trainer.getRoom().isAttackMWN(moWuNiangCreature) && !trainer.getRoom().isDefenseMWN(moWuNiangCreature)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        moWuNiangCreature.fightReady();
        MWNCreature enemy = trainer.getRoom().getEnemy(moWuNiangCreature);
        if (enemy != null) {
            if (RelationshipUtils.isRobotTrainer(enemy.getMaster())) {
                enemy.fightReady();
            }
        }
    }

    /**
     * 玩家驯养师投骰子
     *
     * @param trainer
     */
    public void throwDice(PlayerTrainerCreature trainer) {
        //驯养师是否在战斗场景
        if (!trainer.isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        //驯养师状态检查
        if (!trainer.isSelfRoundTurn()) {
            throw new ManagedException(ManagedErrorCode.NOT_TURN_OPERATOR);
        }
        AbstractRSActSelector actByRoundStage = getActByObjectType(trainer.getRoom().getRoundStage(), ObjectType.PLAYER_TRAINER);
        //考虑前端消息过快，服务端还没走完上个阶段流程,投骰子前这个阶段已经进入尾声，特殊处理
//        if (actByRoundStage.getRoundStage() == RoundStage.Throw_Dice_Before && trainer.isInActStage(ActStage.END)) {
//            actByRoundStage.end(trainer);
//            trainer.syncActStage(null, ActStage.START);
//            actByRoundStage = PlayerRSActManager.getInstance().getActByRoundStage(RoundStage.Throw_Dice_Before.getNextStage());
//        }
        if (!actByRoundStage.isActionAccept(PacketId.PLAYER_THROW_DICE_REQ)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        trainer.setRsTimeOut(System.currentTimeMillis() - 1000);
    }

    /**
     * 魔物娘投骰子，消耗魔物娘卡牌
     *
     * @param trainer
     * @param mwnId
     */
    public void costCardThrowDice(AbstractTrainerCreature trainer, long mwnId) {
        //驯养师是否在战斗场景
        if (!trainer.isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        //驯养师状态检查
        if (!trainer.isSelfRoundTurn()) {
            throw new ManagedException(ManagedErrorCode.NOT_TURN_OPERATOR);
        }
        //处于禁止投骰子状态不能进行操作
        if (trainer.getEffectController().isInStatus(EffectStatus.Limit_Throw_Dice)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        MoWuNiang moWuNiang = trainer.getUseCardStorage().getById(mwnId);
        if (moWuNiang == null) {
            throw new ManagedException(ManagedErrorCode.MWN_NOT_EXISTS);
        }
        //魔物娘已经投骰子不能再次投骰子
        if (moWuNiang.hasThrowDice()) {
            throw new ManagedException(ManagedErrorCode.MWN_HAS_THROW_DICE);
        }
//        //检查魔物娘是否有投骰子技能
//        int mwnResourceId = moWuNiang.getResourceId();
//        MWNResource mwnResource = MWNManager.getInstance().getMWNById(mwnResourceId);
//        if (CollectionUtils.isEmpty(mwnResource.getSkill())) {
//            throw new ManagedException(ManagedErrorCode.MWN_NO_SKILL);
//        }
//        Optional<Integer> any = mwnResource.getSkill().stream().filter(skillId -> skillId != 0 && SkillManager.getInstance().getSkillResource(skillId).getSkillType() == SkillType.Mwn_Throw_Dice).findAny();
//        boolean present = any.isPresent();
//        if (!present) {
//            throw new ManagedException(ManagedErrorCode.MWN_NO_SKILL);
//        }
//        int skillId = any.get();
        //扣除物品
        //trainer.getUseCardStorage().reduceMwn(mwnId);

        //移除的魔物娘进入驯养师召唤列表，以待复活
//        MWNCreature mwnCreature = MWNCreater.create(trainer, moWuNiang, null);
//        mwnCreature.getLifeStats().reduceHp(mwnCreature.getCurrentHp(), null, 0, 0);
//        trainer.addMWN(mwnCreature);

//        RoundFightUtils.trainerCardsChange(trainer, Stream.of(moWuNiang).collect(Collectors.toList()), false, false, Reason.Mwn_Skill_Effect);
        //魔物娘投骰子技能释放后，驯养师获得移动完成后可以在目的地空格两侧放置魔物娘
        //trainer.getEffectController().setStatus(EffectStatus.Arround_Blank_Grid_Set_Mwn, false);
        //考虑前端消息过快，服务端还没走完上个阶段流程,投骰子前这个阶段已经进入尾声，特殊处理
        AbstractRSActSelector actByRoundStage = getActByObjectType(trainer.getRoom().getRoundStage(), ObjectType.PLAYER_TRAINER);
        if (actByRoundStage.getRoundStage() == RoundStage.Throw_Dice_Before) {
            actByRoundStage.end(trainer);
            trainer.syncActStage(null, ActStage.START);
            actByRoundStage = getActByObjectType(RoundStage.Throw_Dice_Before.getNextStage(), ObjectType.PLAYER_TRAINER);
        }
        //玩家驯养师检查是否处于可以进行操作
        if (!actByRoundStage.isActionAccept(PacketId.COST_CARD_THROW_DICE)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }

        if (trainer.getLifeStats().getCurrentMp() < moWuNiang.getResource().getDiceCost()) {
            throw new ManagedException(ManagedErrorCode.SKILL_LACK_MAGIC);
        }
        //魔物娘投骰子不再用技能，扣除魔法
        trainer.getLifeStats().reduceMp(moWuNiang.getResource().getDiceCost(), Reason.Mwn_Throw_Dice, true, true);
        trainer.addConsumeMpRS(moWuNiang.getResource().getDiceCost());
        moWuNiang.setHasThrowDice(true);
//        SkillResource skillResource = SkillManager.getInstance().getSkillResource(skillId);
//        List<Integer> effects = skillResource.getEffects();
//        if (!CollectionUtils.isEmpty(effects)) {
//            for (int effectId : effects) {
//                Effect effect = new Effect(trainer, trainer, SkillManager.getInstance().getEffectResource(effectId).getEffectType().create(), skillId, effectId, Target.valueOf(trainer.getGridId(),
//                        trainer.getObjectId()));
//                trainer.getEffectController().addEffect(effect);
//            }
//        }
        trainer.setRsTimeOut(System.currentTimeMillis() - 1000);
        trainer.getEffectController().setStatus(EffectStatus.Mwn_Throw_Dice, false);
        trainer.setDicePoint(DicePoint.valueOf(moWuNiang.getResource().getDicePoint()));
        trainer.syncActStage(ActStage.READY, ActStage.START);
    }

    /**
     * 退出房间
     *
     * @param trainer
     */
    public void quitRoom(AbstractTrainerCreature trainer) {
        //驯养师状态检查
        if (trainer == null || trainer.getRoom() == null) {
            throw new ManagedException(ManagedErrorCode.ROOM_NOT_EXISTS);
        }
        Room room = trainer.getRoom();
        if (room.isEnd()) {
            if (RelationshipUtils.isPlayerTrainer(trainer)) {
                PlayerTrainerCreature playerTrainerCreature = RelationshipUtils.toPlayerTrainerCreature(trainer);
                if (playerTrainerCreature.getOwner() != null) {
                    playerTrainerCreature.getOwner().setTrainerCreature(null);
                }
            }
            throw new ManagedException(ManagedErrorCode.ROOM_STATE_END);
        }
        room.getRoundFightOver().compareAndSet(false, true);
        //房间还未开启
        if (room.isBeforeStart()) {
            roomManager.removeRoom(room);
            room.onEnd();
            return;
        }
    }

    /**
     * 玩家退出房间
     *
     * @param player
     */
    public void quitRoom(Player player) {
        if (!roomManager.isPlayerInRoom(player)) {
            return;
        }
        Room room = roomManager.getPlayerRoom(player);
        if (Objects.nonNull(room)) {
            if (room.isBeforeStart()) {
                roomManager.removeRoom(room);
                room.onEnd();
                return;
            }
            room.getRoundFightOver().compareAndSet(false, true);
        }
    }

    /**
     * 主动结束当前阶段
     *
     * @param trainer
     */
    public void endRoundStage(AbstractTrainerCreature trainer) {
        //驯养师状态检查
        if (trainer == null || trainer.getRoom() == null) {
            throw new ManagedException(ManagedErrorCode.ROOM_NOT_EXISTS);
        }
        if (!trainer.getRoom().isStart()) {
            throw new ManagedException(ManagedErrorCode.ROOM_STATE_END);
        }
        if (!trainer.isSelfRoundTurn()) {
            throw new ManagedException(ManagedErrorCode.NOT_TURN_OPERATOR);
        }
        trainer.syncActStage(ActStage.START, ActStage.END);
        trainer.setRsTimeOut(System.currentTimeMillis());
        logger.info("收到驯养师结束当前阶段请求:" + trainer.getObjectId() + " 当前阶段:" + trainer.getRoom().getRoundStage() + " 收到时间点: " + System.currentTimeMillis());
    }

    /**
     * 设置魔物娘简易战斗
     *
     * @param player
     * @param openOrClose
     */
    public void mwnSimpleFight(Player player, int openOrClose) {
        if (openOrClose != 0 && openOrClose != 1) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        Map<PlayerCommonType, Integer> commonData = player.getCommonEnt().getCommonData();
        if (CollectionUtils.isEmpty(commonData)) {
            commonData = new HashMap<>();
        }
        Integer open = commonData.get(PlayerCommonType.MWN_SIMPLE_FIGHT);
        if (open != null && open == openOrClose) {
            return;
        }
        commonData.put(PlayerCommonType.MWN_SIMPLE_FIGHT, openOrClose);
        player.getCommonEnt().getChange().set(true);
        PlayerCommonManager.getInstance().update(player);
    }

    /**
     * 对战双方准备完毕通知
     *
     * @param trainer
     */
    public void trainerFightReady(AbstractTrainerCreature trainer) {
        //驯养师状态检查
        if (trainer == null || trainer.getRoom() == null) {
            throw new ManagedException(ManagedErrorCode.ROOM_NOT_EXISTS);
        }
        if (!trainer.getRoom().isInStage(RoundStage.Wait_Trainer_Ready)) {
            throw new ManagedException(ManagedErrorCode.ROOM_IN_FIGHT);
        }
        //trainer.getRoom().trainerFightReady();
        trainer.setOnRoomSate(TrainerOnRoomSate.Ready_Fight);
    }

    /**
     * 返回魔物娘信息
     *
     * @param trainer
     */
    public void dieMwnInfo(AbstractTrainerCreature trainer) {
        Collection<MWNCreature> diedMwn = trainer.getMWN(false);
        List<MoWuNiang> moWuNiangs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(diedMwn)) {
            moWuNiangs = diedMwn.stream().map(creature -> {
                MoWuNiang mwn = creature.getMwn();
                mwn.setId(creature.getObjectId());
                return mwn;
            }).collect(Collectors.toList());
        }
        DiedMwnResp diedMwnResp = DiedMwnResp.valueOf(moWuNiangs);
        PacketSendUtility.sendPacket(trainer, diedMwnResp);
    }

    /**
     * 驯养师抽卡
     *
     * @param trainer
     */
    public void trainerExtractCard(AbstractTrainerCreature trainer) {
        //驯养师是否在战斗场景
        if (!trainer.isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        //驯养师状态检查
        if (!trainer.isSelfRoundTurn()) {
            throw new ManagedException(ManagedErrorCode.NOT_TURN_OPERATOR);
        }
        AbstractRSActSelector actByRoundStage = getActByObjectType(trainer.getRoom().getRoundStage(), ObjectType.PLAYER_TRAINER);
        if (!actByRoundStage.isActionAccept(PacketId.PLAYER_EXTRACT_CARD_REQ)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        if (trainer.syncActStage(ActStage.START, ActStage.END)) {
            roundAddCard(trainer, 1, Reason.Round_Extract_Card);
        }
    }


    /**
     * 玩家断线重连，恢复战斗
     *
     * @param player
     */
    public void trainerReconnect(Player player) {
        //玩家不在房间
        if (!roomManager.isPlayerInRoom(player)) {
            return;
        }
        Room room = roomManager.getPlayerRoom(player);
        //如果房间已经结束，没必要恢复
        if (room.isEnd()) {
            return;
        }
        //先同步房间生物信息
        PlayerTrainerCreature trainerCreature = room.getPlayerTrainerCreature(player);
        player.setSelectedTrainer(trainerCreature.getTrainer());
        player.setTrainerCreature(trainerCreature);
        trainerCreature.setOwner(player);
        //房间还未开启
        if (room.isBeforeStart()) {
            UpdateRoomStateResp roomStateResp = UpdateRoomStateResp.valueOf(room.toRoomVo());
            PacketSendUtility.sendPacket(player, roomStateResp);
            return;
        }
        //房间开启后需同步信息
        {
            //同步场景信息
            room.syncRoomInitInfo(trainerCreature);
            EquipmentInfoResp resp = EquipmentInfoResp.valueOf(trainerCreature.getEquipmentStorage().getEquips(), trainerCreature);
            PacketSendUtility.sendPacket(player, resp);
            //同步所有卡牌信息
            TrainerAllCardsResp trainerAllCardsResp = TrainerAllCardsResp.valueOf(trainerCreature);
            PacketSendUtility.sendPacket(player, trainerAllCardsResp);
            System.err.println("玩家在房间，触发重连：" + player.getName());
        }
    }

    /**
     * 魔物娘之间的战斗对驯养师造成伤害
     *
     * @param trainer
     * @param winner
     */
    public void mwnFightHarmTrainer(AbstractTrainerCreature trainer, MWNCreature winner) {
        int addTrainerHarm = 0;
        if (winner.getEffectController().isInStatus(EffectStatus.Add_Mwn_Trainer_Harm)) {
            MoveAddTrainerHarm addTrainerHarmEffect = (MoveAddTrainerHarm) (winner.getEffectController().getEffect(EffectType.Move_End_Add_Trainer_Harm).getEffectTemplate());
            addTrainerHarm += addTrainerHarmEffect.getAddHarm();
        }
        if (winner.getEffectController().isInStatus(EffectStatus.Alter_Mwn_Trainer_Harm)) {
            Effect effect = winner.getEffectController().getEffect(EffectType.Alter_Mwn_Trainer_Harm);
            addTrainerHarm += effect.getValue();
        }
        int reduceHp = winner.getAttrController().getCurrentAttr(AttrType.Trainer_Harm) + addTrainerHarm;
        reduceHp = reduceHp > trainer.getCurrentHp() ? trainer.getCurrentHp() : reduceHp;
        int mwnFightHarmTrainerSkill = ConfigValueManager.getInstance().getIntConfigValue("MwnFightHarmTrainer");
        //此处方便客户端走技能效果，特殊处理
        DamageSkillResult damageSkillResult = DamageSkillResult.valueOf(winner.getObjectId(), trainer.getObjectId(), reduceHp, mwnFightHarmTrainerSkill);
        List<Long> targets = new ArrayList<>();
        targets.add(trainer.getObjectId());
        RoundFightUtils.sendUseSkill(winner, mwnFightHarmTrainerSkill, targets, Stream.of(damageSkillResult).collect(Collectors.toList()), true);
        trainer.getLifeStats().reduceHp(reduceHp, winner, Reason.Mwn_Fight, false);
    }


    /**
     * 查询回合战斗日志记录
     *
     * @param trainer
     */
    public void offlineRoundLog(AbstractTrainerCreature trainer) {
        //驯养师状态检查
        if (trainer == null || trainer.getRoom() == null) {
            throw new ManagedException(ManagedErrorCode.ROOM_NOT_EXISTS);
        }
        if (trainer.getRoom().isOnState(RoomState.END)) {
            throw new ManagedException(ManagedErrorCode.ROOM_STATE_END);
        }
        Optional<AbstractTrainerCreature> any = trainer.getRoom().getTrainers().stream().filter(tc -> tc.getObjectId() != trainer.getObjectId()).findAny();
        if (any.isPresent()) {
            AbstractTrainerCreature trainerCreature = any.get();
            ServerAskCliRoundLogResp resp = ServerAskCliRoundLogResp.valueOf();
            PacketSendUtility.sendPacket(trainerCreature, resp);
        }
    }

    /**
     * 获取前端回合战斗日志，转发
     *
     * @param trainer
     */
    public void serverAskCliRoundLog(AbstractTrainerCreature trainer, ServerAskCliRoundLogReq req) {
        //驯养师状态检查
        if (trainer == null || trainer.getRoom() == null) {
            throw new ManagedException(ManagedErrorCode.ROOM_NOT_EXISTS);
        }
        Optional<AbstractTrainerCreature> any = trainer.getRoom().getTrainers().stream().filter(tc -> tc.getObjectId() != trainer.getObjectId()).findAny();
        if (any.isPresent()) {
            AbstractTrainerCreature trainerCreature = any.get();
            OffLineRoundLogResp resp = OffLineRoundLogResp.valueOf(req.getLogs());
            PacketSendUtility.sendPacket(trainerCreature, resp);
        }
    }

    /**
     * 跳过魔物娘战斗
     *
     * @param trainer
     */
    public void escapeMwnFight(AbstractTrainerCreature trainer) {
        //驯养师状态检查
        if (trainer == null || trainer.getRoom() == null) {
            throw new ManagedException(ManagedErrorCode.ROOM_NOT_EXISTS);
        }
        //驯养师是否在战斗场景
        if (!trainer.isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        if (!trainer.isSelfRoundTurn()) {
            throw new ManagedException(ManagedErrorCode.NOT_TURN_OPERATOR);
        }
        //驯养师状态检查
        if (!trainer.getRoom().isInStage(RoundStage.MOVE_END)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        MWNCreature enmwn = trainer.getRoom().getWorldMapInstance().getMWNByGridId(trainer.getGridId());
        //最终停留格有敌方魔物娘，没有召唤魔物娘战斗将受到敌方魔物娘对我方驯养师伤害
        if (!trainer.isCallMwnFight(trainer.getGridId()) && RelationshipUtils.judgeRelationship(trainer, enmwn, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)) {
            trainer.setCallMwnFight(trainer.getGridId());
            RoundFightService.getInstance().mwnFightHarmTrainer(trainer, enmwn);
        }
    }

    /**
     * 魔物娘技能效果表现完成，通知后端
     *
     * @param trainer
     * @param mwnId
     */
    public void mwnSkillEffectOver(AbstractTrainerCreature trainer, long mwnId) {
        //驯养师状态检查
        if (trainer == null || trainer.getRoom() == null) {
            throw new ManagedException(ManagedErrorCode.ROOM_NOT_EXISTS);
        }
        //驯养师是否在战斗场景
        if (!trainer.isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        AbstractCreature creature = trainer.getWorldMapInstance().getCreatureById(mwnId);
        if (!RelationshipUtils.isMWN(creature)) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        if (!RelationshipUtils.judgeRelationship(trainer, creature, RelationshipUtils.Relationships.SELF_TRAINER_MWN)) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        if (!creature.isAlreadyDead()) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        creature.toRemove();
        creature.getController().delete();
    }

    /**
     * 魔物娘是否在战斗中被召唤过
     *
     * @param trainerCreature
     * @param mwnCreature
     * @return
     */
    public boolean hasMwnCalled(AbstractTrainerCreature trainerCreature, MWNCreature mwnCreature) {
        return trainerCreature.getRoom().getMwnDeadTimes(mwnCreature.getMwn()) > 0;
    }

    /**
     * 获取驯养师根据自身魔物娘添加职业元素属性
     *
     * @param trainerCreature
     * @return
     */
    public List<Attr> mwnAddTrainerAttrs(AbstractTrainerCreature trainerCreature) {
        //职业或元素点
        List<Attr> jobOrEleAttr = new ArrayList<>();
        Collection<MWNCreature> mwnCreatures = trainerCreature.getAllMwn();
        if (!CollectionUtils.isEmpty(mwnCreatures)) {
            mwnCreatures.stream().filter(mwn -> mwn.isAlive() || !MWNService.getInstance().deadRemoveTrainerEleAttr(mwn)).filter(mwn -> mwn.hasEleAttr() || mwn.hasJobAttr()).forEach(mwn -> {
                AttrType.getAllJobType().forEach(type -> {
                    if (mwn.hasAttr(type)) {
                        jobOrEleAttr.add(Attr.valueOf(type, mwn.getAttrController().getCurrentAttr(type)));
                    }
                });
                AttrType.getAllEleType().forEach(type -> {
                    if (mwn.hasAttr(type)) {
                        jobOrEleAttr.add(Attr.valueOf(type, mwn.getAttrController().getCurrentAttr(type)));
                    }
                });
            });
        }
        return jobOrEleAttr;
    }

    /**
     * 魔物娘对战添加援护,援护魔物娘进入墓地，但是不会触发死亡亡语
     *
     * @param trainerCreature
     * @param supportId
     */
    public void mwnSupport(AbstractTrainerCreature trainerCreature, long supportId) {
        //驯养师是否在战斗场景
        if (!trainerCreature.isInWorldMap()) {
            throw new ManagedException(ManagedErrorCode.ERROR_MSG);
        }
        if (trainerCreature.getEffectController().isInStatus(EffectStatus.Limit_Use_Equip)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        //驯养师状态检查
        AbstractRSActSelector actByRoundStage = getActByObjectType(trainerCreature.getRoom().getRoundStage(), ObjectType.PLAYER_TRAINER);
        if (!trainerCreature.isSelfRoundTurn() && !actByRoundStage.isActionAccept(PacketId.MWN_SUPPORT_REQ)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_OP_LIMIT);
        }
        //魔物娘检查
        if (!trainerCreature.getUseCardStorage().hasMwn(supportId)) {
            throw new ManagedException(ManagedErrorCode.MWN_NOT_EXISTS);
        }
        MWNCreature fightMWN = trainerCreature.getFightMWN();
        if (Objects.isNull(fightMWN)) {
            throw new ManagedException(ManagedErrorCode.MWN_NOT_EXISTS);
        }
        //移除的魔物娘进入驯养师召唤列表，不走死亡流程，进入墓地以待复活
        MoWuNiang moWuNiang = trainerCreature.getUseCardStorage().getById(supportId);

        trainerCreature.getUseCardStorage().reduceMwn(supportId);
        PacketSendUtility.sendPacket(trainerCreature, RoundCardUpdateResp.valueOf(trainerCreature.getObjectId(),
                Stream.of(moWuNiang).collect(Collectors.toList()), false, false, Reason.Player_Op));
        MWNCreature enemy = trainerCreature.getRoom().getEnemy(fightMWN);
        //对方魔物娘有魅惑效果，
        if (enemy.getEffectController().isInStatus(EffectStatus.Confuse)) {
            return;
        }

        MWNCreature mwnCreature = MWNCreater.create(trainerCreature, moWuNiang, null);

        mwnCreature.getLifeStats().setCurrentHp(0);
        trainerCreature.addMWN(mwnCreature);
        fightMWN.setSupporter(moWuNiang.getId());

        //接受援助后战斗魔物娘属性会发生变化
        //TODO 目前写死添加属性，后续补充对应逻辑
        List<Attr> addAttrs = new ArrayList<>();
        addAttrs.add(Attr.valueOf(AttrType.Attack, 20));
        addAttrs.add(Attr.valueOf(AttrType.Max_Hp, 100));

        fightMWN.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Mwn_Support), addAttrs);
        //有模仿效果，拷贝一份
        if (mwnCreature.getEffectController().isInStatus(EffectStatus.Imitate)) {
            List<Attr> attrs = enemy.getAttrController().getAttrByAttrEffectId(AttrEffectId.valueOf(AttrEffectType.Mwn_Support));
            if (attrs.size() > 0) {
                List<Attr> copy = new ArrayList<>(attrs.size());
                attrs.forEach(it -> copy.add(it.clone()));
                fightMWN.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Mwn_Support_Imitate), copy);
            }
        }
        if (enemy.getSupporter() > 0) {
            MWNCreature mwn = enemy.getMaster().getMwn(enemy.getSupporter());
            if (mwn.getEffectController().isInStatus(EffectStatus.Imitate)) {
                List<Attr> attrs = fightMWN.getAttrController().getAttrByAttrEffectId(AttrEffectId.valueOf(AttrEffectType.Mwn_Support));
                if (attrs.size() > 0) {
                    List<Attr> copy = new ArrayList<>(attrs.size());
                    attrs.forEach(it -> copy.add(it.clone()));
                    enemy.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Mwn_Support_Imitate), copy);
                }
            }
        }
    }

    /**
     * 驯养师开局先后手顺序
     *
     * @param room
     */
    public void trainerOrder(Room room) {
        Map<Long, Integer> trainerOrderInfo = new HashMap<>();
        int[] randomDice = randomDiffDice(room.getTrainers().size());
        int length = randomDice.length - 1;
        room.getTrainers().forEach(trainerCreature -> trainerOrderInfo.put(trainerCreature.getObjectId(),
                randomDice[length - room.getTrainers().indexOf(trainerCreature)]));
        TrainerOrderInfoResp trainerOrderInfoResp = TrainerOrderInfoResp.valueOf(trainerOrderInfo);
        room.getTrainers().forEach(trainerCreature -> PacketSendUtility.sendPacket(trainerCreature, trainerOrderInfoResp));
        room.getTrainers().forEach(room::syncRoomInitInfo);
    }

    /**
     * 初始化第一回合魔法值
     *
     * @param trainer
     */
    public void initFirstRoundMp(AbstractTrainerCreature trainer) {
        trainer.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Round_Effect), Lists.newArrayList(Attr.valueOf(AttrType.Max_Mp, 4)), true, false);
    }

    /**
     * 回合初魔法上限增加，恢复魔法
     *
     * @param trainer
     */
    public void recoverMpOnNewRound(AbstractTrainerCreature trainer) {
        //魔法上限加1，恢复魔法
        if (trainer.getEffectController().isInStatus(EffectStatus.Limit_Mp_Inc)) {
            return;
        }
        int currentMaxMp = trainer.getAttrController().getCurrentAttr(AttrType.Max_Mp);
        //第五回合开始添加魔法上限
        if (currentMaxMp < RoundFightUtils.ROUND_MAX_MP && trainer.getRoom().getRound() > RoundFightUtils.ROUND_BEGING_ADD_MP) {
            trainer.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Round_Effect), Attr.valueOf(AttrType.Max_Mp, currentMaxMp + 1), true);
        }
        trainer.getLifeStats().setCurrentMp(trainer.getAttrController().getCurrentAttr(AttrType.Max_Mp));
        trainer.getLifeStats().sendLifeChange(Reason.Round_Add_Mp);
        System.err.println("回合初驯养师魔法值：" + trainer.getLifeStats().getCurrentMp());
        //每回合初同步技能建筑cd
        PacketSendUtility.sendPacket(trainer, SkillBuildCdResp.valueOf(trainer));
    }
}
