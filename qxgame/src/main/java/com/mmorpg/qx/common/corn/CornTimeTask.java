package com.mmorpg.qx.common.corn;

/**
 * @author wang ke
 * @description: 定时执行任务
 * @since 14:19 2021/4/6
 */
public interface CornTimeTask {
    /**
     * 零点任务,该方法要处理掉所有异常
     */
    default void everyDayZero() {

    }

    /**
     * 任务排序，值越大越后执行
     *
     * @return
     */
    default int getOrder() {
        return 0;
    }
}
