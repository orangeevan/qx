package com.mmorpg.qx.module.account.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * 踢人下线
 *
 * @author wang ke
 * @since v1.0 2018年3月8日
 */
@SocketPacket(packetId = PacketId.KICK_REASON_RESP)
public class KickReasonResp {
    /**
     * 提下线原因
     */
    @Protobuf(description = " 1：重复登录被踢下线  2:长时间没心跳被踢   3：GM踢人   4：请求退出游戏")
    private int reason;

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

}
