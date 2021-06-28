package clientRobot;

import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.core.WresponsePacket;
import com.mmorpg.qx.module.player.packet.CreatePlayerReq;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.baidu.bjf.remoting.protobuf.Codec;

@ChannelHandler.Sharable
public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final WresponsePacket packet = (WresponsePacket) msg;
        System.err.println("客户端接收到服务端的消息: "+packet.getPacketId());
        if(packet.getPacketId() == PacketId.CREATE_PLAYER_REQ){
            System.err.println("消息内容流: "+packet.getData());
            Codec<CreatePlayerReq> codec = ProtobufProxy.create(CreatePlayerReq.class, false, null);
            CreatePlayerReq decode = codec.decode(packet.getData());
            System.err.println("客户端接收到服务端消息: "+decode.toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
