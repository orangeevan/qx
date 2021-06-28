package com.mmorpg.qx.common.socket.server;

import com.mmorpg.qx.common.socket.core.SessionManager;
import com.mmorpg.qx.common.socket.core.Wsession;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息会话管理handler
 * @author wangke
 * @since v1.0 2016年6月3日
 *
 */
@Sharable
@Component
public class SessionHandler extends ChannelInboundHandlerAdapter {

	@Autowired
	private SessionManager sessionManager;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Wsession session = Wsession.valueOf(ctx.channel());
		sessionManager.add(session);
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		sessionManager.remove(ctx.channel().id());
	}

}