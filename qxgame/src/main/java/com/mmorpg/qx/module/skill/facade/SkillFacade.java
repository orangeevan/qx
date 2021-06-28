package com.mmorpg.qx.module.skill.facade;

import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.annotation.SocketClass;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.skill.packet.BuildingUseSkillReq;
import com.mmorpg.qx.module.skill.packet.CreatureEffectReq;
import com.mmorpg.qx.module.skill.packet.UseSkillReq;
import com.mmorpg.qx.module.skill.service.SkillService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@SocketClass
public class SkillFacade {

    private Logger logger = SysLoggerFactory.getLogger(SkillFacade.class);

    @Autowired
    private SkillService skillService;
    @Autowired
    private PlayerManager playerManager;

    @SocketMethod
    public void useSkill(Wsession session, UseSkillReq req) {
        // 1.检查参数合法性
        if (req.getSkillId() == 0) {
            ErrorPacketUtil.sendError(session, ManagedErrorCode.PARAMETER_ILLEGAL);
            return;
        }
        Player player = playerManager.getPlayerBySession(session);
        try {
            skillService.useSkill(player.getTrainerCreature(), req);
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(session, e.getCode());
        } catch (Throwable e) {
            logger.error("未知错误.", e);
            ErrorPacketUtil.sendError(session, ManagedErrorCode.SYS_ERROR);
        }
    }

    @SocketMethod
    public void buildingUseSkill(Wsession session, BuildingUseSkillReq req) {
        try {
            Player player = playerManager.getPlayerBySession(session);
            skillService.buildingUseSkill(player.getTrainerCreature(), req);
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(session, e.getCode());
        } catch (Throwable e) {
            logger.error("未知错误.", e);
            ErrorPacketUtil.sendError(session, ManagedErrorCode.SYS_ERROR);
        }
    }

    @SocketMethod
    public void effectInfo(Wsession session, CreatureEffectReq req) {
        try {
            Player player = playerManager.getPlayerBySession(session);
            skillService.selfMwnEffectInfo(player.getTrainerCreature(), req.getObjectId());
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(session, e.getCode());
        } catch (Throwable e) {
            logger.error("未知错误.", e);
            ErrorPacketUtil.sendError(session, ManagedErrorCode.SYS_ERROR);
        }
    }
}
