package com.mmorpg.qx.module.player.facade;

import com.haipaite.common.event.anno.ReceiverAnno;
import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.annotation.SocketClass;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.player.event.PlayerHeartbeatEvent;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.player.packet.*;
import com.mmorpg.qx.module.player.service.PlayerService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 角色门面方法. 通信socket,事件event,计划任务cron 三者触发的业务起点
 *
 * @author wang ke
 * @since v1.0 2019年2月7日
 */
@Component
@SocketClass
public class PlayerFacade {

    private Logger logger = SysLoggerFactory.getLogger(PlayerFacade.class);

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerManager playerManager;

    @SocketMethod
    public void getPlayerList(Wsession session, GetPlayerListReq req) {
        try {
            System.err.println("收到3号协议");
            GetPlayerListResp resp = playerService.getPlayerList(session, req);
            session.sendPacket(resp);
        } catch (ManagedException e) {
            session.sendPacket(ErrorPacketUtil.getErrorPacket(e.getCode()));
        } catch (Throwable e) {
            logger.error("未知错误.", e);
            session.sendPacket(ErrorPacketUtil.getExceptionErrorPacket());
        }
    }

    @SocketMethod
    public void createPlayer(Wsession session, CreatePlayerReq req) {
        try {
            playerService.createPlayer(session, req);
        } catch (ManagedException e) {
            session.sendPacket(ErrorPacketUtil.getErrorPacket(e.getCode()));
        } catch (Throwable e) {
            logger.error("未知错误.", e);
            session.sendPacket(ErrorPacketUtil.getExceptionErrorPacket());
        }
    }

    @SocketMethod
    public void login(Wsession session, LoginPlayerReq req) {
        try {

            playerService.login(session, req.getPlayerId());
        } catch (ManagedException e) {
            session.sendPacket(ErrorPacketUtil.getErrorPacket(e.getCode()));
        } catch (Throwable e) {
            logger.error("登录错误", e);
            session.sendPacket(ErrorPacketUtil.getExceptionErrorPacket());
        }
    }

    //@SocketMethod
    public void enterWorld(Wsession session, EnterWorldReq req) {
        Player player = playerManager.getPlayerBySession(session);
        try {
            playerService.enterWorld(player, req);
        } catch (ManagedException e) {
            session.sendPacket(ErrorPacketUtil.getErrorPacket(e.getCode()));
        } catch (Throwable e) {
            logger.error("进入地图错误", e);
            session.sendPacket(ErrorPacketUtil.getExceptionErrorPacket());
        }
    }

    @SocketMethod
    public void playerHeartBeat(Wsession session, PlayerHeartBeatReq req) {
        Player player = playerManager.getPlayerBySession(session);
        try {
            playerService.updatePlayerHeartBeat(player);
        } catch (Exception e) {
            logger.error("心跳处理错误", e);
        }
    }

    @SocketMethod
    public void playerQuit(Wsession wsession, PlayerQuitReq req) {
        Player player = playerManager.getPlayerBySession(wsession);
        try {
            playerService.quitGame(player);
        } catch (Exception e) {
            logger.error("退出游戏异常", e);
        }
    }

    @ReceiverAnno
    public void playerHeatBeatEvent(PlayerHeartbeatEvent event) {
        Player player = event.getPlayer();
        try {
            playerService.heartbeatEventHandle(player, System.currentTimeMillis());
        } catch (Exception e) {
            logger.error("心跳事件处理错误", e);
        }
    }
}
