package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import java.util.Objects;

/**
 * @author wang ke
 * @description: 玩家离线前后战斗日志
 * @since 14:51 2021/1/6
 */
@SocketPacket(packetId = PacketId.PLAYER_OFF_LINE_ROUND_LOG_RESP)
public class OffLineRoundLogResp {

    @Protobuf(description = "离线前后战斗记录")
    private byte[] logs;

    public static OffLineRoundLogResp valueOf(byte[] logs) {
        OffLineRoundLogResp resp = new OffLineRoundLogResp();
        if (Objects.nonNull(logs)) {
            resp.logs = logs;
        }else{
            resp.logs = new byte[0];
        }
        return resp;
    }

    public byte[] getLogs() {
        return logs;
    }

    public void setLogs(byte[] logs) {
        this.logs = logs;
    }
}

