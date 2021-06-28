package com.mmorpg.qx.module.account.facade;

import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.annotation.SocketClass;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.account.packet.LoginAuthReq;
import com.mmorpg.qx.module.account.packet.LoginAuthResp;
import com.mmorpg.qx.module.account.service.AccountService;

@Component
@SocketClass
public class AccountFacade {
	private Logger logger = SysLoggerFactory.getLogger(AccountFacade.class);

	@Autowired
	private AccountService accountService;

	/**
	 * 登陆验证
	 * 
	 * @param session
	 */
	@SocketMethod
	public void loginAuth(Wsession session, LoginAuthReq req) {
		try {
			LoginAuthResp resp = accountService.loginAuth(session, req);
			session.sendPacket(resp);
			System.err.println("收到1号消息，返回2号消息，登录结果："+resp.getResult());
		} catch (ManagedException e) {
			session.sendPacket(ErrorPacketUtil.getErrorPacket(e.getCode()));
		} catch (Throwable e) {
			logger.error("未知错误.", e);
			session.sendPacket(ErrorPacketUtil.getExceptionErrorPacket());
		}

	}
}
