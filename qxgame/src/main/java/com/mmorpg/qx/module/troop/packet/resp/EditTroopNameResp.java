package com.mmorpg.qx.module.troop.packet.resp;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;

/**
 * @author zhang peng
 * @description
 * @since 16:14 2021/4/9
 */
@SocketPacket(packetId = PacketId.EDIT_TROOP_NAME_RESP)
public class EditTroopNameResp {

    @Protobuf(description = "编队类型")
    private int type;

    @Protobuf(description = "编队索引")
    private int index;

    @Protobuf(description = "编队名字")
    private String name;

    public static EditTroopNameResp valueOf(int type, int index, String name) {
        EditTroopNameResp resp = new EditTroopNameResp();
        resp.type = type;
        resp.index = index;
        resp.name = name;
        return resp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
