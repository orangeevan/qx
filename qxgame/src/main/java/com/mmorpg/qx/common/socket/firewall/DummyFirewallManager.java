package com.mmorpg.qx.common.socket.firewall;


import com.mmorpg.qx.common.socket.core.WrequestPacket;
import com.mmorpg.qx.common.socket.core.Wsession;

public class DummyFirewallManager implements FirewallManager {

	@Override
	public boolean packetFilter(Wsession session, WrequestPacket packet) {
		return true;
	}

}
