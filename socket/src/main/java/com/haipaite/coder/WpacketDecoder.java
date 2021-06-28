 package com.haipaite.coder;
 
 import com.haipaite.core.WrequestPacket;
 import com.haipaite.utils.IpUtils;
 import io.netty.buffer.ByteBuf;
 import io.netty.channel.ChannelHandlerContext;
 import io.netty.handler.codec.ByteToMessageDecoder;
 import java.util.List;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 
 
 
 
 
 
 
 
 public class WpacketDecoder
   extends ByteToMessageDecoder
 {
   private Logger logger = LoggerFactory.getLogger("socket");
 
 
 
   
   private static int MAX_SIZE = 2097152;
   
   private static int MIN_SIZE = 2;
 
   
   private static final int MIN_READABLE = 6;
 
 
   
   public WpacketDecoder(int maxLength) {
     if (maxLength <= MIN_SIZE) {
       this.logger.error(String.format("maxLength error! length[%s] MIN_SIZE[%s]", new Object[] { Integer.valueOf(maxLength), Integer.valueOf(MIN_SIZE) }));
     }
     MAX_SIZE = maxLength;
   }
 
   
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
     in.markReaderIndex();
     int readableBytes = in.readableBytes();
     if (readableBytes < 6) {
       return;
     }
     
     int size = in.readInt();
     if (size < MIN_SIZE) {
       
       this.logger.warn(String.format("ip[%s] size error.config_minsize[%s] size[%s]", new Object[] {
               IpUtils.getIp(ctx.channel().remoteAddress().toString()), Integer.valueOf(MIN_SIZE), Integer.valueOf(size) }));
       ctx.close();
     } 
     
     if (size >= MAX_SIZE) {
       
       this.logger.warn(String.format("ip[%s] size error.config_maxsize[%s] size[%s]", new Object[] {
               IpUtils.getIp(ctx.channel().remoteAddress().toString()), Integer.valueOf(MAX_SIZE), Integer.valueOf(size) }));
       ctx.close();
     } 
     if (in.readableBytes() < size) {
       in.resetReaderIndex();
       
       return;
     } 
     short packetId = in.readShort();
     
     byte[] data = new byte[in.readableBytes()];
     in.readBytes(data);
     
     WrequestPacket wp = WrequestPacket.valueOf(packetId, data);
     out.add(wp);
     System.err.println("****服务端收到信息*****: " + wp.getPacketId());
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\coder\WpacketDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */