package com.mmorpg.qx.module.shop.facade;

import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.shop.packet.ShopBuyReq;
import com.mmorpg.qx.module.shop.service.ShopService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商城
 * @author wang ke
 * @since v1.0 2018年3月21日
 *
 */
@Component
public class ShopFacade {

	private Logger logger = SysLoggerFactory.getLogger(ShopFacade.class);

	@Autowired
	private ShopService shopService;
	@Autowired
	private PlayerManager playerManager;

	@SocketMethod
	public void shopBuy(Wsession session, ShopBuyReq req) {
		// 1.检查参数合法性
		if (10000 < req.getAmount() || req.getAmount() < 0) {
			ErrorPacketUtil.sendError(session, ManagedErrorCode.PARAMETER_ILLEGAL);
			return;
		}
		Player player = playerManager.getPlayerBySession(session);
		try {
			shopService.shopBuy(player, req.getAmount(), req.getShopId(), req.isQuickBuy());
		} catch (ManagedException e) {
			ErrorPacketUtil.sendError(session, e.getCode());
		} catch (Throwable e) {
			logger.error("未知错误.", e);
			ErrorPacketUtil.sendError(session, ManagedErrorCode.SYS_ERROR);
		}
	}
}
