package com.mmorpg.qx.common.socket.server;

import io.netty.channel.ChannelHandlerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户自定义handler管理器
 * @author wangke
 * @since v1.0 2016年6月3日
 *
 */
@Component
public final class CustomHandlerManager {

	private List<ChannelHandlerAdapter> handlers = new ArrayList<ChannelHandlerAdapter>();

	@PostConstruct
	public void init() {
		doInit();
	}

	private void doInit() {
		// TODO
	}

	public List<ChannelHandlerAdapter> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<ChannelHandlerAdapter> handlers) {
		this.handlers = handlers;
	}

}
