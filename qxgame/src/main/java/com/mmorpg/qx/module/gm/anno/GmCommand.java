package com.mmorpg.qx.module.gm.anno;

import java.lang.annotation.*;

/**
 * @author wang ke
 * @description:简化命令模式，每个GM方法通过注解返回匹配类型信息
 * @since 10:09 2020-08-29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmCommand {
    /**
     * GM指令调用样式
     * @return
     */
    public String mode();

    /***
     * 如有需要可以自备正则表达式匹配指令
     * @return
     */
    public String pattern() default "";
}
