package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

import java.util.Objects;

/**
 * @author wang ke
 * @description: 客户端返回收到所有回合战斗数据
 * @since 17:46 2021/1/6
 */
@SocketPacket(packetId = PacketId.SERVER_ASK_CLI_ROUND_LOG_REQ)
public class ServerAskCliRoundLogReq {
    @Protobuf(description = "离线前后战斗记录")
    private byte[] logs;

    public static ServerAskCliRoundLogReq valueOf(byte[] logs) {
        ServerAskCliRoundLogReq req = new ServerAskCliRoundLogReq();
        if (Objects.nonNull(logs)) {
            req.logs = logs;
        } else {
            req.logs = new byte[0];
        }
        return req;    }

    public byte[] getLogs() {
        return logs;
    }

    public void setLogs(byte[] logs) {
        this.logs = logs;
    }
}
