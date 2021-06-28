package com.mmorpg.qx.common.socket.annotation;

import java.lang.annotation.*;

/**
 * 模块声明
 * 
 * @author wangke
 * @since v1.0 2017-1-19
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SocketMethod {
}