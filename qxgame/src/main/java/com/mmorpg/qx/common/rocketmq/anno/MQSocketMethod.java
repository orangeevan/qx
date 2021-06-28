package com.mmorpg.qx.common.rocketmq.anno;

import java.lang.annotation.*;

/**
 * @author wang ke
 * @description: MQ业务方法标记
 * @since 15:35 2021/6/10
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MQSocketMethod {

}
