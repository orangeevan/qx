package com.mmorpg.qx.module.player.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * 获取该账号所有的玩家,后缀接REQ,方便安装业务排序
 * 
 * @author wang ke
 * @since v1.0 2019年2月8日
 *
 */
@SocketPacket(packetId = PacketId.GET_PLAYER_LIST_REQ)
@ProtobufClass
public class GetPlayerListReq {

}
