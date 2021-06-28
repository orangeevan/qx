package com.mmorpg.qx.module.object.gameobject.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.object.gameobject.packet.vo.AttrVo;

import java.util.ArrayList;
import java.util.List;

@SocketPacket(packetId = PacketId.CREATURE_ATTR_CHANGE_RESP)
public class CreatureAttrChangeResp {
	@Protobuf(description = " 2：魔物娘 4: 机器人驯养师  7: 玩家驯养师")
	private int objectType;

	@Protobuf(description = "地图对象唯一id")
	private long id;

	@Protobuf
	private List<AttrVo> attrs = new ArrayList<>();

	public List<AttrVo> getAttrs() {
		return attrs;
	}

	public void setAttrs(List<AttrVo> attrs) {
		this.attrs = attrs;
	}

	public int getObjectType() {
		return objectType;
	}

	public void setObjectType(int objectType) {
		this.objectType = objectType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
