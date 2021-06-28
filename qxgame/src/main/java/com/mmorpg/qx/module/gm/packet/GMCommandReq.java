package com.mmorpg.qx.module.gm.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

import java.util.List;

/**
 * @author wang ke
 * @description:
 * @since 09:38 2020-08-29
 */
@SocketPacket(packetId = PacketId.GM_COMMAND_REQ)
public class GMCommandReq {
    @Protobuf(description = "方法名")
    private String method;

    @Protobuf(description = "参数数组")
    private List<String> params;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }
}
