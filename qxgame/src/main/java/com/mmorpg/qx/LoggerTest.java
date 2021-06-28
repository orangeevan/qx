package com.mmorpg.qx;

import com.mmorpg.qx.common.SystemOut;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import org.slf4j.Logger;

public class LoggerTest {
	private static Logger logger = SysLoggerFactory.getLogger(LoggerTest.class);

	public static void main(String[] args) {
		SystemOut.println(LoggerTest.class.getName());
		logger.debug("[[" + LoggerTest.class + " " + "debug");
		logger.error("[[" + LoggerTest.class + " " + "error");
		SystemOut.println("[[" + LoggerTest.class + " " + "System.out");
		SystemOut.println("[[" + LoggerTest.class + " " + "System.err");
	}
}
