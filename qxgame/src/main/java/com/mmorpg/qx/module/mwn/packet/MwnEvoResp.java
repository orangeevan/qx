package com.mmorpg.qx.module.mwn.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 魔物娘进化结果
 * @since 15:09 2021/3/18
 */
@SocketPacket(packetId = PacketId.MWN_SKILL_EVO_RESP)
public class MwnEvoResp {

    @Protobuf(description = "驯养师id")
    private long trainerId;

    @Protobuf(description = "0:没有质变 1:进化 2:退化")
    private int evo;

    @Protobuf(description = "魔物娘变化列表")
    private List<GameUtil.LongVo> mwnList;

    @Protobuf(description = "0:替换 1:占领 2:失去")
    private int occupy;

    @Protobuf(description = "元素类型 -1:没有元素变化")
    private int attrType;

    @Protobuf(description = "元素值变化前")
    private int attrValueBefore;

    @Protobuf(description = "元素值变化后")
    private int attrValue;

    public static MwnEvoResp valueOf(AbstractTrainerCreature trainerCreature, List<MWNCreature> mwnCreatures, int evo, int occupy, Attr alterBefore) {
        MwnEvoResp resp = new MwnEvoResp();
        resp.evo = evo;
        if (mwnCreatures == null) {
            resp.mwnList = new ArrayList<>();
        } else {
            resp.mwnList = mwnCreatures.stream().map(mwnCreature -> GameUtil.LongVo.valueOf(mwnCreature.getObjectId())).collect(Collectors.toList());
        }
        resp.occupy = occupy;
        resp.attrType = alterBefore != null ? alterBefore.getType().value() : -1;
        resp.attrValueBefore = alterBefore != null ? alterBefore.getValue() : 0;
        resp.attrValue = alterBefore != null ? trainerCreature.getAttrController().getCurrentAttr(alterBefore.getType()) : 0;
        resp.trainerId = trainerCreature.getObjectId();
        return resp;
    }

    public int getEvo() {
        return evo;
    }

    public void setEvo(int evo) {
        this.evo = evo;
    }

    public List<GameUtil.LongVo> getMwnList() {
        return mwnList;
    }

    public void setMwnList(List<GameUtil.LongVo> mwnList) {
        this.mwnList = mwnList;
    }

    public long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(long trainerId) {
        this.trainerId = trainerId;
    }

    public int getOccupy() {
        return occupy;
    }

    public void setOccupy(int occupy) {
        this.occupy = occupy;
    }

    public int getAttrType() {
        return attrType;
    }

    public void setAttrType(int attrType) {
        this.attrType = attrType;
    }

    public int getAttrValueBefore() {
        return attrValueBefore;
    }

    public void setAttrValueBefore(int attrValueBefore) {
        this.attrValueBefore = attrValueBefore;
    }

    public int getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(int attrValue) {
        this.attrValue = attrValue;
    }
}
