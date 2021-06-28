package com.mmorpg.qx.module.roundFight.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.packet.vo.MoWuNiangVo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 返回死亡魔物娘
 * @since 17:31 2020-11-19
 */
@SocketPacket(packetId = PacketId.DIED_MWN_RESP)
public class DiedMwnResp {
    @Protobuf(description = "魔物娘信息")
    private List<MoWuNiangVo> dieMwn;

    public static DiedMwnResp valueOf(Collection<MoWuNiang> mwn) {
        DiedMwnResp resp = new DiedMwnResp();
        List<MoWuNiangVo> mwnList = new ArrayList<>();
        resp.setDieMwn(mwnList);
        if (!CollectionUtils.isEmpty(mwn)) {
            mwnList.addAll(mwn.stream().map(MoWuNiangVo::valueOf).collect(Collectors.toList()));
        }
        return resp;
    }

    public List<MoWuNiangVo> getDieMwn() {
        return dieMwn;
    }

    public void setDieMwn(List<MoWuNiangVo> dieMwn) {
        this.dieMwn = dieMwn;
    }
}
