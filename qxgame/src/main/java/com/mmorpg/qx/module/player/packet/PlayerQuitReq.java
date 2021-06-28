package com.mmorpg.qx.module.player.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description: 客户端请求退出游戏
 * @since 10:49 2021/5/6
 */
@SocketPacket(packetId = PacketId.PLAYER_QUIT_REQ)
@ProtobufClass
public class PlayerQuitReq {
    
}
