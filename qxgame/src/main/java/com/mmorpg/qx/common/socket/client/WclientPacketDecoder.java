package com.mmorpg.qx.common.socket.client;

import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.core.WresponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * |size-int-4|packetId-short-2|data|
 * 
 * @author wangke
 * @since v1.0 2018年1月31日
 *
 */
public class WclientPacketDecoder extends ByteToMessageDecoder {

	private Logger logger = SysLoggerFactory.getLogger(WclientPacketDecoder.class);

	/**
	 * length+packetId = 6
	 */
	private static final int MIN_READABLE = 4 + 2;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		in.markReaderIndex();
		int readableBytes = in.readableBytes();

		if (readableBytes < MIN_READABLE) {
			return;
		}

		int size = Integer.reverseBytes(in.readInt());
		if (in.readableBytes() < size) {
			in.resetReaderIndex();
			return;
		}

		short packetId = Short.reverseBytes(in.readShort());

		byte[] data = new byte[in.readableBytes()];
		in.readBytes(data);

		WresponsePacket response = WresponsePacket.valueOf(packetId, data);
		out.add(response);

	}

}
