 package com.haipaite.client;

 import com.haipaite.core.WrequestPacket;
 import io.netty.buffer.ByteBuf;
 import io.netty.channel.ChannelHandlerContext;
 import io.netty.handler.codec.MessageToByteEncoder;









 public class WclientPacketEncoder
   extends MessageToByteEncoder<WrequestPacket>
 {
   protected void encode(ChannelHandlerContext ctx, WrequestPacket msg, ByteBuf out) throws Exception {
     out.writeInt((msg.getData()).length + 2);
     out.writeShort(msg.getPacketId());
     out.writeBytes(msg.getData());
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\client\WclientPacketEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */