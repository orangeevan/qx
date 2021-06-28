package com.mmorpg.qx.module.mwn.packet;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description:
 * @since 10:37 2021/3/13
 */
@SocketPacket(packetId = PacketId.MWN_LIST_REQ)
@ProtobufClass
public class MwnListReq {

}
