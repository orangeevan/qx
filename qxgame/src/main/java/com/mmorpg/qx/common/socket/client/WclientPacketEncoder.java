package com.mmorpg.qx.common.socket.client;

import com.mmorpg.qx.common.socket.core.WrequestPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * @author wangke
 * @since v1.0 2018年2月6日
 *
 */
public class WclientPacketEncoder extends MessageToByteEncoder<WrequestPacket> {

	@Override
	protected void encode(ChannelHandlerContext ctx, WrequestPacket msg, ByteBuf out) throws Exception {
		// length
		out.writeInt(Integer.reverseBytes(msg.getData().length + 2));
		out.writeShort(Short.reverseBytes(msg.getPacketId()));
		out.writeBytes(msg.getData());
	}

}
