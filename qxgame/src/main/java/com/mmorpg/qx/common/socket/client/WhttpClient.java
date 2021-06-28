package com.mmorpg.qx.common.socket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

import java.util.ArrayList;
import java.util.List;

/**
 * http异步客服端
 * 
 * @author wangke
 * @since v1.0 2017年6月3日
 *
 */
public class WhttpClient {

	private static EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
	private static Bootstrap b = new Bootstrap();

	private Channel channel;

	private List<ChannelHandler> extraHandlers = new ArrayList<ChannelHandler>();

	public void addChannelHandler(ChannelHandler ch) {
		extraHandlers.add(ch);
	}

	public ChannelFuture connect(int port, String host) throws InterruptedException {
		b.group(eventLoopGroup).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel sc) throws Exception {
						sc.pipeline().addLast("decoder", new HttpResponseDecoder());
						sc.pipeline().addLast("http-aggregator", new HttpObjectAggregator(512 * 1024));
						sc.pipeline().addLast("encoder", new HttpRequestEncoder());
						for (ChannelHandler ch : extraHandlers) {
							sc.pipeline().addLast(ch);
						}
					}
				});
		ChannelFuture channelFuture = b.connect(host, port).sync();
		channel = channelFuture.channel();
		return channelFuture;
	}

	public void sendPacket(Object packet) {
		channel.writeAndFlush(packet);
	}


	/**
	 * 关闭客户端连接，线程池
	*/
	public void shutdown(){
		try{
			channel.close();
		}finally {
			eventLoopGroup.shutdownGracefully();
		}

	}
}
