package com.mmorpg.qx.common.socket.coder;

import com.mmorpg.qx.common.socket.core.WresponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author wangke
 * @since v1.0 2018年2月6日
 */
public class WpacketEncoder extends MessageToByteEncoder<WresponsePacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, WresponsePacket msg, ByteBuf out) throws Exception {
        // length
        System.err.println("服务端发送消息: " + msg.getPacketId() + " 消息体长度：" + (msg.getData().length + 2));
        out.writeInt(msg.getData().length + 2);
        out.writeShort(msg.getPacketId());
        out.writeBytes(msg.getData());
    }

}
