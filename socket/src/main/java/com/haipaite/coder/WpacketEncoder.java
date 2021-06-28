 package com.haipaite.coder;
 
 import com.haipaite.core.WresponsePacket;
 import io.netty.buffer.ByteBuf;
 import io.netty.channel.ChannelHandlerContext;
 import io.netty.handler.codec.MessageToByteEncoder;
 
 
 
 
 
 
 
 
 
 public class WpacketEncoder
   extends MessageToByteEncoder<WresponsePacket>
 {
   protected void encode(ChannelHandlerContext ctx, WresponsePacket msg, ByteBuf out) throws Exception {
     System.err.println("服务端发送消息: " + msg.getPacketId() + " 消息体长度：" + ((msg.getData()).length + 2));
     out.writeInt((msg.getData()).length + 2);
     out.writeShort(msg.getPacketId());
     out.writeBytes(msg.getData());
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\coder\WpacketEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */