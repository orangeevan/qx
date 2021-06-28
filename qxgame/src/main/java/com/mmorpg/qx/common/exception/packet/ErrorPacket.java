package com.mmorpg.qx.common.exception.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 错误码推送消息包
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
@SocketPacket(packetId = PacketId.ERROR_PACKET)
public class ErrorPacket {
    @Protobuf(description = "错误码")
    private int code;

    @Protobuf(description = "参数个数")
    private int paramNum;

    @Protobuf(description = "参数")
    private List<String> params;

    public ErrorPacket() {

    }

    public ErrorPacket(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void initParams(String... params) {
        this.params = new ArrayList<>();
        if (Objects.nonNull(params)) {
            paramNum = params.length;
            for (String param : params) {
                this.params.add(param);
            }
        }
    }
}
