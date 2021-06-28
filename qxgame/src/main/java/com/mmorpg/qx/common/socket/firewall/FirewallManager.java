package com.mmorpg.qx.common.socket.firewall;

import com.mmorpg.qx.common.socket.core.WrequestPacket;
import com.mmorpg.qx.common.socket.core.Wsession;
/**
 * 防火墙管理接口
 * 
 * @author wangke
 * @since v1.0 2016-1-20
 * 
 */
public interface FirewallManager {

	/**
	 * 是否允许该消息通过
	 * 
	 * @param session
	 * @param packet
	 * @return
	 */
	boolean packetFilter(Wsession session, WrequestPacket packet);

}
