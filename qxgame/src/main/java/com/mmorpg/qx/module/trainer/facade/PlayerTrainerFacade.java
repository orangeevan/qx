package com.mmorpg.qx.module.trainer.facade;

import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.annotation.SocketClass;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.trainer.packet.*;
import com.mmorpg.qx.module.trainer.service.PlayerTrainerService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wang ke
 * @description:
 * @since 15:01 2020-08-19
 */

@Component
@SocketClass
public class PlayerTrainerFacade {

    private static final Logger logger = SysLoggerFactory.getLogger(PlayerTrainerFacade.class);

    @Autowired
    PlayerTrainerService playerTrainerService;

    @SocketMethod
    public void createPlayerTrainer(Wsession wsession, CreateTrainerReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            playerTrainerService.createTrainer(player, req.getTrainerId());
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(wsession, e.getCode());
        } catch (Exception e) {
            logger.error("createPlayerTrainer", e);
        }
    }

    /**
     * 驯养师列表
     */
    @SocketMethod
    public void trainerList(Wsession wsession, TrainerListReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            playerTrainerService.trainerList(player);
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(wsession, e.getCode());
        } catch (Exception e) {
            logger.error("驯养师列表异常", e);
        }
    }

    /**
     * 驯养师解锁
     */
    @SocketMethod
    public void trainerUnlock(Wsession wsession, TrainerUnlockReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            playerTrainerService.trainerUnlock(player, req.getResourceId(), req.getChipId());
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(wsession, e.getCode());
        } catch (Exception e) {
            logger.error("驯养师解锁异常", e);
        }
    }

    /**
     * 驯养师更换皮肤
     */
    @SocketMethod
    public void trainerChangeSkin(Wsession wsession, TrainerChangeSkinReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            playerTrainerService.trainerChangeSkin(player, req.getTrainerId(), req.getSkinId());
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(wsession, e.getCode());
        } catch (Exception e) {
            logger.error("驯养师更换皮肤异常", e);
        }
    }

    /**
     * 驯养师出战
     */
    @SocketMethod
    public void trainerGoFight(Wsession wsession, TrainerGoFightReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            playerTrainerService.trainerGoFight(player, req.getTrainerId());
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(wsession, e.getCode());
        } catch (Exception e) {
            logger.error("驯养师出战异常", e);
        }
    }

}
