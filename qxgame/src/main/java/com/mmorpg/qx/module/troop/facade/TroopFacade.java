package com.mmorpg.qx.module.troop.facade;

import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.annotation.SocketClass;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.troop.packet.req.*;
import com.mmorpg.qx.module.troop.service.TroopService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 编队facade
 *
 * @author zhang peng
 * @since 15:49 2021/5/11
 */
@Component
@SocketClass
public class TroopFacade {

    private static final Logger logger = SysLoggerFactory.getLogger(TroopFacade.class);

    @Autowired
    private TroopService troopService;

    /**
     * 解锁编队
     */
    @SocketMethod
    public void unlockTroop(Wsession wsession, UnlockTroopReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            troopService.unlockTroop(player, req.getType(), req.getIndex(), req.getName());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("创建编队异常", e);
        }
    }

    /**
     * 修改编队
     */
    @SocketMethod
    public void alterTroop(Wsession wsession, AlterTroopReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            troopService.alterTroop(player, req.getType(), req.getIndex(), req.getCards());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("修改编队异常", e);
        }
    }

    /**
     * 编辑编队名字
     */
    @SocketMethod
    public void editTroopName(Wsession wsession, EditTroopNameReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            troopService.editTroopName(player, req.getType(), req.getIndex(), req.getName());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("编辑编队名字异常", e);
        }
    }

    /**
     * 编队出战
     */
    @SocketMethod
    public void troopGoFight(Wsession wsession, TroopGoFightReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            troopService.troopGoFight(player, req.getType(), req.getIndex());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("编队出战异常", e);
        }
    }

    /**
     * 编队切换技能
     */
    @SocketMethod
    public void troopChangeSkill(Wsession wsession, TroopChangeSkillReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            troopService.troopChangeSkill(player, req.getType(), req.getIndex(), req.getSkillId());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("编队切换技能异常", e);
        }
    }
}
