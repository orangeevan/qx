package com.mmorpg.qx.common.exception;

import java.util.concurrent.ConcurrentHashMap;

import com.mmorpg.qx.common.exception.packet.ErrorPacket;
import com.mmorpg.qx.common.socket.core.Wsession;

/**
 * error消息包工具类
 * @author wang ke
 * @since v1.0 2018年3月7日
 *
 */
public class ErrorPacketUtil {
	private static final ConcurrentHashMap<Integer, ErrorPacket> CACHE = new ConcurrentHashMap<>();

	public static ErrorPacket getErrorPacket(int errorCode) {
		if (!CACHE.containsKey(errorCode)) {
			ErrorPacket ep = new ErrorPacket();
			ep.setCode(errorCode);
			CACHE.put(errorCode, ep);
		}
		return CACHE.get(errorCode);
	}

	private static final ErrorPacket EXCEPTION_PACKET = new ErrorPacket(ManagedErrorCode.SYS_ERROR);

	public static ErrorPacket getExceptionErrorPacket() {
		return EXCEPTION_PACKET;
	}

	public static void sendError(Wsession session, int code, String... params) {
		ErrorPacket packet = getErrorPacket(code);
		packet.initParams(params);
		session.sendPacket(packet);
	}

}
