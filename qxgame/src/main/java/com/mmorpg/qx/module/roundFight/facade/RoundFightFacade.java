package com.mmorpg.qx.module.roundFight.facade;

import com.haipaite.common.event.anno.ReceiverAnno;
import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.common.socket.annotation.SocketClass;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.object.controllers.event.MWNDeadEvent;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.mmorpg.qx.module.player.event.LoginEvent;
import com.mmorpg.qx.module.player.event.ReconnetEvent;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.roundFight.packet.*;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.trainer.module.PlayerTrainer;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author wang ke
 * @description: 房间战斗门面
 * @since 18:08 2020-08-17
 */
@Component
@SocketClass
public class RoundFightFacade {

    private static final Logger logger = SysLoggerFactory.getLogger(RoundFightFacade.class);

    @Autowired
    private RoundFightService roundFightService;

    /***
     * 玩家请求开房间
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void createRoom(Wsession wsession, PlayerCreateRoomReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.createRoom(player, req);
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("创建房间", e);
        }
    }

    /***
     * 玩家开启战斗
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void roundFightStart(Wsession wsession, PlayerRoundFightStartReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            System.err.println(String.format("%s 玩家开启战斗，房间号： %s", player.getName(), req.getRoom()));
            roundFightService.roundFightStart(player.getTrainerCreature());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("开启战斗", e);
        }
    }

    /***
     *  请求房间信息
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void roomInfo(Wsession wsession, RoomInfoReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.roomInfos(player);
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("房间信息", e);
        }
    }

    /***
     * 请求加入房间
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void joinRoom(Wsession wsession, PlayerJoinRoomReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            PlayerTrainer trainer = player.getTrainerCreature(req.getTrainerId());
            roundFightService.joinRoom(trainer, req.getRoomId(), req.getTroopType(), req.getSkillId());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("加入房间", e);
        }
    }

    @SocketMethod
    public void searchRoom(Wsession wsession, PlayerSearchRoomReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            Room room = roundFightService.searchRoom(player, req.getRoomId());
            if (room != null) {
                PacketSendUtility.sendPacket(player, UpdateRoomStateResp.valueOf(room.toRoomVo()));
            }
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("房间信息", e);
        }
    }

    /**
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void callMWN(Wsession wsession, PlayerCallSummonReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.callMWN(player.getTrainerCreature(), req.getMwnId(), req.getGridId());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode(), managedException.getParams());
        } catch (Exception e) {
            logger.error("", e);
        }

    }

    /**
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void mwnWearEquip(Wsession wsession, MwnWearEquipReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.mwnWearEquip(player.getTrainerCreature(), req.getMwnId(), req.getEquipId());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("魔物娘添加装备", e);
        }
    }


    /**
     * 客户端只要准备好战斗，待双方进入战斗准备后，自动开启战斗，无须前端开启战斗
     *
     * @param wsession
     * @param req
     */
    //@SocketMethod
    public void mwnFight(Wsession wsession, MwnFightStartReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.mwnFight(player.getTrainerCreature(), req.getMwnId());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void move(Wsession wsession, TrainerMoveReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.trainerMove(player.getTrainerCreature(), req);
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void mwnFightReady(Wsession wsession, MWNFightReady req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.mwnFightReady(player.getTrainerCreature(), req);
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /***
     * 投骰子
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void throwDice(Wsession wsession, PlayerThrowDiceReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.throwDice(player.getTrainerCreature());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /***
     * 魔物娘投骰子技能
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void costCardThrowDice(Wsession wsession, CostCardDiceReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.costCardThrowDice(player.getTrainerCreature(), req.getMwnId());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @ReceiverAnno
    public void onKillMWN(MWNDeadEvent event) {
        try {
            if (Objects.nonNull(event.getKiller())) {
                WorldMapService.getInstance().handleDie(event.getKiller(), event.getDeadMWN());
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 请求退出战斗
     *
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void quitRoom(Wsession wsession, PlayerQuitRoomReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            if (Objects.isNull(player.getTrainerCreature())) {
                roundFightService.quitRoom(player);
            } else {
                roundFightService.quitRoom(player.getTrainerCreature());
            }
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 结束当前阶段
     *
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void endRoundStage(Wsession wsession, NotifyEndRoundReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.endRoundStage(player.getTrainerCreature());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 请求装备信息
     *
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void equipInfo(Wsession wsession, EquipmentInfoReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            PlayerTrainerCreature trainerCreature = player.getTrainerCreature();
            EquipmentInfoResp resp = EquipmentInfoResp.valueOf(trainerCreature.getEquipmentStorage().getEquips(), trainerCreature);
            PacketSendUtility.sendPacket(player, resp);
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 设置魔物娘简易战斗
     *
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void mwnSimpleFight(Wsession wsession, MwnSimpleFightReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.mwnSimpleFight(player, req.getOpenOrClose());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 驯养师请求抽卡
     *
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void trainerExtractCard(Wsession wsession, TrainerExtractCardReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.trainerExtractCard(player.getTrainerCreature());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 驯养师准备战斗
     *
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void trainerReadyFight(Wsession wsession, TrainerFightReadyReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.trainerFightReady(player.getTrainerCreature());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 请求死亡魔物娘信息
     *
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void dieMwnInfo(Wsession wsession, DiedMwnReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.dieMwnInfo(player.getTrainerCreature());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 战场驯养师卡组和手牌信息
     *
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void trainerAllCards(Wsession wsession, TrainerAllCardsReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            //同步所有卡牌信息
            TrainerAllCardsResp trainerAllCardsResp = TrainerAllCardsResp.valueOf(player.getTrainerCreature());
            PacketSendUtility.sendPacket(player, trainerAllCardsResp);
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 玩家重连恢复房间战斗
     *
     * @param loginEvent
     */
    @ReceiverAnno
    public void playerLoginEvent(LoginEvent loginEvent) {
//        Player player = loginEvent.getPlayer();
//        roundFightService.trainerReconnect(player);
    }

    @ReceiverAnno
    public void reconnetEvent(ReconnetEvent event) {
        Player player = event.getPlayer();
        roundFightService.trainerReconnect(player);
    }

    /**
     * 请求回合战斗日志记录
     *
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void offLineRoundLog(Wsession wsession, OffLineRoundLogReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.offlineRoundLog(player.getTrainerCreature());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 收到客户端告目前战斗日志记录
     *
     * @param wsession
     * @param req
     */
    public void serverAskCliRoundLog(Wsession wsession, ServerAskCliRoundLogReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.serverAskCliRoundLog(player.getTrainerCreature(), req);
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 跳过战斗
     *
     * @param wsession
     * @param req
     */
    @SocketMethod
    public void escapeMwnFight(Wsession wsession, EscapeMwnFightReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.escapeMwnFight(player.getTrainerCreature());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @SocketMethod
    public void mwnSkillEffectActOver(Wsession wsession, MwnSEActOverReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.mwnSkillEffectOver(player.getTrainerCreature(), req.getMwnId());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @SocketMethod
    public void mwnSupport(Wsession wsession, MwnSupportReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            roundFightService.mwnSupport(player.getTrainerCreature(), req.getSupportId());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
