package com.mmorpg.qx.common.socket.coder;

import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.core.WrequestPacket;
import com.mmorpg.qx.common.socket.utils.IpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * |size-int-4|packetId-short-2|data|
 *
 * @author wangke
 * @since v1.0 2018年1月31日
 */
public class WpacketDecoder extends ByteToMessageDecoder {

    private Logger logger = SysLoggerFactory.getLogger(WpacketDecoder.class);

    /**
     * 2m
     */
    private static int MAX_SIZE = 2 * 1024 * 1024;

    private static int MIN_SIZE = 2;

    /**
     * length+packetId = 6
     */
    private static final int MIN_READABLE = 4 + 2;

    public WpacketDecoder(int maxLength) {
        if (maxLength <= MIN_SIZE) {
            logger.error(String.format("maxLength error! length[%s] MIN_SIZE[%s]", maxLength, MIN_SIZE));
        }
        MAX_SIZE = maxLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        List<Integer> intdatas = new ArrayList<>();
        int readableBytes = in.readableBytes();
//        ByteBuf copy = in.copy();
//        byte[] sdata = new byte[copy.readableBytes()];
//        copy.readBytes(sdata);
//        for (byte b : sdata) {
//            intdatas.add(b & 0xff);
//        }
//        Arrays.asList(intdatas).stream().forEach(b -> System.err.print(b));
//        System.err.println();
        //System.err.println("readableBytes字节数: "+readableBytes);
        if (readableBytes < MIN_READABLE) {
            return;
        }

        int size = in.readInt();
        //C# 整数翻转
        //size = Integer.reverseBytes(size);
        if (size < MIN_SIZE) {
            // 包长度不够
            logger.warn(String.format("ip[%s] size error.config_minsize[%s] size[%s]", IpUtils.getIp(ctx.channel().remoteAddress().toString()), MIN_SIZE, size));
            ctx.close();
        }

        if (size >= MAX_SIZE) {
            // 超过最大长度
            logger.warn(String.format("ip[%s] size error.config_maxsize[%s] size[%s]", IpUtils.getIp(ctx.channel().remoteAddress().toString()), MAX_SIZE, size));
            ctx.close();
        }
        if (in.readableBytes() < size) {
            in.resetReaderIndex();
            return;
        }
        //C#特殊处理，高低位反转
        //short packetId = Short.reverseBytes(in.readShort());
        short packetId = in.readShort();
        intdatas.add((int) packetId);
        System.err.println("服务端收到消息: " + packetId + " 消息体长度：" + (in.readableBytes() + 2) + "|" + size);
        byte[] data = new byte[size - 2];
        in.readBytes(data);
        WrequestPacket wp = WrequestPacket.valueOf(packetId, data);
        out.add(wp);
        //System.err.println("****服务端收到信息*****: "+wp.getPacketId());
    }

}
