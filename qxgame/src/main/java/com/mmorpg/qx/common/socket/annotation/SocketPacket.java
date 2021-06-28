package com.mmorpg.qx.common.socket.annotation;

import java.lang.annotation.*;

/**
 * 通信包声明
 *
 * @author wangke
 * @since v1.0 2017-1-19
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SocketPacket {
    short packetId();

    short module() default (short) 0;

}