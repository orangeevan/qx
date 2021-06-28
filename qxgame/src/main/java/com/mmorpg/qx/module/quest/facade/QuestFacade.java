package com.mmorpg.qx.module.quest.facade;

import com.haipaite.common.event.anno.ReceiverAnno;
import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.condition.ConditionType;
import com.mmorpg.qx.module.player.event.LoginEvent;
import com.mmorpg.qx.module.player.event.MidNightEvent;
import com.mmorpg.qx.module.player.event.PlayerLevelUpEvent;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.quest.packet.AcceptQuestReq;
import com.mmorpg.qx.module.quest.packet.CompleteQuestReq;
import com.mmorpg.qx.module.quest.packet.RewardQuestReq;
import com.mmorpg.qx.module.quest.service.QuestService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestFacade {
    private static final Logger logger = SysLoggerFactory.getLogger(QuestFacade.class);

    @Autowired
    private QuestService questService;
    @Autowired
    private PlayerManager playerManager;

    @SocketMethod
    public void accept(Wsession session, AcceptQuestReq req) {
        Player player = playerManager.getPlayerBySession(session);
        try {
            questService.acceptQuest(player, req.getId());
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(session, e.getCode());
        } catch (Exception e) {
            logger.error("玩家接取任务异常", e);
            ErrorPacketUtil.sendError(session, ManagedErrorCode.SYS_ERROR);
        }
    }

    @SocketMethod
    public void reward(Wsession session, RewardQuestReq req) {
        Player player = playerManager.getPlayerBySession(session);
        try {
            questService.reward(player, req.getId());
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(session, e.getCode());
        } catch (Exception e) {
            logger.error("玩家任务异常", e);
            ErrorPacketUtil.sendError(session, ManagedErrorCode.SYS_ERROR);
        }
    }

    @SocketMethod
    public void complete(Wsession session, CompleteQuestReq req) {
        Player player = playerManager.getPlayerBySession(session);
        try {
            questService.completeQuest(player, req.getId());
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(session, e.getCode());
        } catch (Exception e) {
            logger.error("玩家任务异常", e);
            ErrorPacketUtil.sendError(session, ManagedErrorCode.SYS_ERROR);
        }
    }

    @ReceiverAnno
    public void loginEvent(LoginEvent loginEvent) {
        try {
            Player player = loginEvent.getPlayer();
            // 玩家任务更新
            player.getQuestBox().clearDaily();
            // 刷新任务池
            questService.refreshQuests(player);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @ReceiverAnno
    public void midNightEvent(MidNightEvent midNightEvent) {
        try {
            Player player = midNightEvent.getPlayer();
            // 刷新任务池
            player.getQuestBox().clearDaily();
            questService.refreshQuests(player);
        } catch (Exception e) {
            logger.error("", e);
        }

    }

    @ReceiverAnno
    public void onLevelChanged(PlayerLevelUpEvent playerLevelUpEvent) {
        try {
            Player player = playerLevelUpEvent.getPlayer();
            if (playerLevelUpEvent.getOldLevel() < player.getPlayerEnt().getLevel()) {
                questService.refreshQuestsByConditionType(player, ConditionType.PLAYER_LEVEL);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
