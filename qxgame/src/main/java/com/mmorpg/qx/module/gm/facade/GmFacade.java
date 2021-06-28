package com.mmorpg.qx.module.gm.facade;

import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.common.socket.annotation.SocketClass;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.gm.anno.GmCommand;
import com.mmorpg.qx.module.gm.packet.GMCommandReq;
import com.mmorpg.qx.module.gm.packet.GMErrorResp;
import com.mmorpg.qx.module.gm.packet.ServerKickOff;
import com.mmorpg.qx.module.gm.service.GmService;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author wangke
 * @since v1.0 2019/4/2
 */
@Component
@SocketClass
public class GmFacade {

    private Logger logger = SysLoggerFactory.getLogger(GmFacade.class);

    @Autowired
    GmService service;

    @Autowired
    PlayerManager playerManager;

    @SocketMethod
    public void serverKickOff(Wsession wsession, ServerKickOff req) {
        service.kickOffAll(wsession, playerManager.getPlayerBySession(wsession));
    }

    @SocketMethod
    public void command(Wsession wsession, GMCommandReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            Method method = ReflectionUtils.findMethod(GmService.class, req.getMethod().trim(), null);
            if (method == null) {
                ErrorPacketUtil.sendError(wsession, ManagedErrorCode.GM_COMMAND_NOT_EXISTS);
            }
            GmCommand annotation = method.getAnnotation(GmCommand.class);
            if (!StringUtils.isEmpty(annotation.pattern())) {
                StringBuilder builder = new StringBuilder();
                builder.append(req.getMethod());
                if (req.getParams() != null) {
                    req.getParams().forEach(object -> builder.append(" ").append(object));
                }
                if (!builder.toString().matches(annotation.pattern())) {
                    PacketSendUtility.sendPacket(player, GMErrorResp.valueOf(annotation.mode()));
                }
            }
            if (method.getParameterCount() != 0 && method.getParameterCount() != req.getParams().size() + 1) {
                PacketSendUtility.sendPacket(player, GMErrorResp.valueOf(annotation.mode()));
            }
            ReflectionUtils.invokeMethod(method, service, player, req.getParams() != null ? req.getParams().toArray() : null);
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
