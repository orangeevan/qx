package com.mmorpg.qx.common.corn;

import com.haipaite.common.scheduler.Scheduled;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description:
 * @since 15:57 2021/3/12
 */
@Component
public class CornManager extends InstantiationAwareBeanPostProcessorAdapter {

    //缓存所有定时执行任务
    private List<CornTimeTask> timedTasks = new ArrayList<>();

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, String beanName) throws BeansException {
        if (CornTimeTask.class.isAssignableFrom(bean.getClass())) {
            CornTimeTask task = (CornTimeTask) bean;
            addTask(task);
        }
        return super.postProcessAfterInstantiation(bean, beanName);
    }

    private void addTask(CornTimeTask task) {
        timedTasks.add(task);
        timedTasks = timedTasks.stream().sorted(Comparator.comparingInt(CornTimeTask::getOrder)).collect(Collectors.toList());
    }

    /**
     * 秒  分  时 天 月 周几
     * 每天0点任务
     */
    @Scheduled(name = "everyDayZero", value = "0 0 0 * * *")
    private void everyDayZero() {
        timedTasks.forEach(CornTimeTask::everyDayZero);
    }

}
