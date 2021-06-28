package com.mmorpg.qx.module.integral.packet.resp;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.integral.packet.vo.IntegralVo;

import java.util.List;

/**
 * @author zhang peng
 * @description:
 * @since 11:33 2021/3/5
 */

@SocketPacket(packetId = PacketId.INTEGRAL_UPDATE_RESP)
public class IntegralUpdateResp {

    @Protobuf(description = "更新后的积分")
    private List<IntegralVo> integrals;

    public static IntegralUpdateResp valueOf(List<IntegralVo> integrals) {
        IntegralUpdateResp resp = new IntegralUpdateResp();
        resp.setIntegrals(integrals);
        return resp;
    }

    public List<IntegralVo> getIntegrals() {
        return integrals;
    }

    public void setIntegrals(List<IntegralVo> integrals) {
        this.integrals = integrals;
    }
}
