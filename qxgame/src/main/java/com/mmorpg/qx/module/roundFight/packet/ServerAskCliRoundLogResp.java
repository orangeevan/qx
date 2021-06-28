package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 服务端向客户端请求所有回合战斗数据
 * @since 17:49 2021/1/6
 */
@SocketPacket(packetId = PacketId.SERVER_ASK_CLI_ROUND_LOG_RESP)
@ProtobufClass
public class ServerAskCliRoundLogResp {

    public static ServerAskCliRoundLogResp valueOf() {
        ServerAskCliRoundLogResp resp = new ServerAskCliRoundLogResp();
        return resp;
    }
}
