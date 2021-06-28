package com.mmorpg.qx.common.socket.core;

import com.mmorpg.qx.common.logger.SysLoggerFactory;
import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理
 *
 * @author wangke
 * @since v1.0 2016年6月3日
 */
@Component
public class SessionManager {
    private static final Logger logger = SysLoggerFactory.getLogger(SessionManager.class);

    /**
     * 所有会话
     */
    private ConcurrentHashMap<ChannelId, Wsession> allSessions = new ConcurrentHashMap<ChannelId, Wsession>();

    public void add(Wsession session) {
        if (!allSessions.containsKey(session.getChannel().id())) {
            allSessions.put(session.getChannel().id(), session);
        } else {
            // 不应该进入到这里
            logger.error(String.format("channelId[%s],ip[%s]重复注册sessionManager", session.getChannel().id().asShortText(), session.getChannel().remoteAddress()));
        }
    }

    public int ipSessionCount(String ip) {
        int count = 0;
        for (Wsession session : allSessions.values()) {
            if (session.getChannel().remoteAddress().toString().contains(ip)) {
                count++;
            }
        }
        return count;
    }

    public Wsession getSession(ChannelId channelId) {
        return allSessions.get(channelId);
    }

    public void remove(ChannelId id) {
        allSessions.remove(id);
    }

}
