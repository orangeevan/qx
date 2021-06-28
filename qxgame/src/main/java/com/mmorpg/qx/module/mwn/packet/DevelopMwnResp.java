package com.mmorpg.qx.module.mwn.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.gameobject.packet.vo.AttrVo;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author zhang peng
 * @description:
 * @since 15:39 2021/3/11
 */
@SocketPacket(packetId = PacketId.DEVELOP_MWN_RESP)
public class DevelopMwnResp {

    @Protobuf(description = "魔物娘id")
    private long mwnId;
    @Protobuf(description = "魔物娘经验")
    private int exp;
    @Protobuf(description = "魔物娘等级")
    private int level;
    @Protobuf(description = "魔物娘突破阶数")
    private int casteLv;
    @Protobuf(description = "魔物娘潜能星数")
    private int starLv;
    @Protobuf(description = "魔物娘属性")
    private List<AttrVo> voList;

    public static DevelopMwnResp valueOf(MoWuNiang mwn, List<AttrVo> voList) {
        DevelopMwnResp resp = new DevelopMwnResp();
        resp.setMwnId(mwn.getId());
        resp.setExp(mwn.getExp());
        resp.setLevel(mwn.getLevel());
        resp.setCasteLv(mwn.getCasteLv());
        resp.setStarLv(mwn.getStarLv());
        if (!CollectionUtils.isEmpty(voList)) {
            resp.setVoList(voList);
        }
        return resp;
    }

    public long getMwnId() {
        return mwnId;
    }

    public void setMwnId(long mwnId) {
        this.mwnId = mwnId;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCasteLv() {
        return casteLv;
    }

    public void setCasteLv(int casteLv) {
        this.casteLv = casteLv;
    }

    public int getStarLv() {
        return starLv;
    }

    public void setStarLv(int starLv) {
        this.starLv = starLv;
    }

    public List<AttrVo> getVoList() {
        return voList;
    }

    public void setVoList(List<AttrVo> voList) {
        this.voList = voList;
    }
}
