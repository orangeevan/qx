package com.mmorpg.qx.common.session;

import com.mmorpg.qx.common.socket.core.WrequestPacket;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.common.socket.firewall.FirewallManager;
import org.springframework.stereotype.Component;

import com.mmorpg.qx.common.PacketId;


@Component
public class FirewallManagerImpl implements FirewallManager {

	@Override
	public boolean packetFilter(Wsession session, WrequestPacket packet) {
		if (packet.getPacketId() == PacketId.LOGIN_AUTH_REQ) {
			// 第一个验证消息允许通过
			return true;
		}
		if (SessionUtils.isLoginAuth(session)) {
			return true;
		}
		// 未通过验证的消息,不允许
		return false;
	}

}
