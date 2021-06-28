 package com.haipaite.client;
 
 import com.haipaite.core.WresponsePacket;
 import io.netty.buffer.ByteBuf;
 import io.netty.channel.ChannelHandlerContext;
 import io.netty.handler.codec.ByteToMessageDecoder;
 import java.util.List;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 
 
 
 
 
 
 
 
 
 
 public class WclientPacketDecoder
   extends ByteToMessageDecoder
 {
   private Logger logger = LoggerFactory.getLogger("socket");
 
 
   
   private static final int MIN_READABLE = 6;
 
 
   
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
     in.markReaderIndex();
     int readableBytes = in.readableBytes();
     
     if (readableBytes < 6) {
       return;
     }
     
     int size = in.readInt();
     if (in.readableBytes() < size) {
       in.resetReaderIndex();
       
       return;
     } 
     short packetId = in.readShort();
     
     byte[] data = new byte[in.readableBytes()];
     in.readBytes(data);
     
     WresponsePacket response = WresponsePacket.valueOf(packetId, data);
     out.add(response);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\client\WclientPacketDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */