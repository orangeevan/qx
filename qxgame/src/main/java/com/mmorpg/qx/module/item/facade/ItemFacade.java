package com.mmorpg.qx.module.item.facade;


import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.annotation.SocketClass;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.item.packet.req.BackPackItemUseReq;
import com.mmorpg.qx.module.item.service.ItemService;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author wang ke
 * @description: 道具、编队门面
 * @since 17:46 2020-08-06
 */
@Component
@SocketClass
public class ItemFacade {

    private static final Logger logger = SysLoggerFactory.getLogger(ItemFacade.class);

    @Autowired
    private ItemService service;

    /**
     * 使用仓库道具
     */
    @SocketMethod
    public void useBackpackItem(Wsession wsession, BackPackItemUseReq req){
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            service.useItem(player, req.getObjectId(), req.getNum(), req.getUse());
        } catch (ManagedException managedException) {
            ErrorPacketUtil.sendError(wsession, managedException.getCode());
        } catch (Exception e) {
            logger.error("使用仓库道具异常", e);
        }
    }

}
