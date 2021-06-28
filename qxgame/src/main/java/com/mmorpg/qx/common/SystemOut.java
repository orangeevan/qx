package com.mmorpg.qx.common;

import com.mmorpg.qx.Debug;

/**
 * 
 * @author wang ke
 * @since v1.0 2018年3月24日
 *
 */
public class SystemOut {
	/**
	 * 这个存在的意义是.这个项目里不允许出现直接sysout的调用.防止开发时调试,在现网却忘记注释造成日志污染
	 * @param str
	 */
	public static void println(Object str) {
		if (Debug.debug) {
			System.out.println(str);
		}
	}
}
