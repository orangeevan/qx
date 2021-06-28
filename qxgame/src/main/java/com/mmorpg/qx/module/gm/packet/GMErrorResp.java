package com.mmorpg.qx.module.gm.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author wang ke
 * @description:
 * @since 10:30 2020-08-29
 */
@SocketPacket(packetId = PacketId.GM_COMMAND_ERROR_RESP)
public class GMErrorResp {
    @Protobuf(description = "GM指令正确的格式")
    private String mode;

    public static GMErrorResp valueOf(String mode) {
        GMErrorResp resp = new GMErrorResp();
        resp.setMode(mode);
        return resp;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
