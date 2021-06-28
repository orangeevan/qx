package com.mmorpg.qx.common.socket.firewall;


import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.core.SessionManager;
import com.mmorpg.qx.common.socket.core.Wsession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 流量控制防火墙
 *
 * @author wangke
 * @since v1.0 2016-1-14
 */
@Sharable
@Component
public class FlowFirewall extends ChannelInboundHandlerAdapter {

    private static final Logger logger = SysLoggerFactory.getLogger(FlowFirewall.class);

    @Autowired
    private SessionManager sessionManager;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Wsession session = sessionManager.getSession(ctx.channel().id());
        if (session != null) {
            ByteBuf buf = (ByteBuf) msg;
            boolean outOfLine = session.getFirewallRecord().check(buf.readableBytes());
            if (outOfLine) {
                // 违规
                logger.error(String.format("id[%s],ip[%s]流量违规!record[%s]", session.getChannel().id(), session.getChannel().remoteAddress(), session.getFirewallRecord()));
                ctx.channel().close();
                return;
            }

        }
        ctx.fireChannelRead(msg);
    }
}
