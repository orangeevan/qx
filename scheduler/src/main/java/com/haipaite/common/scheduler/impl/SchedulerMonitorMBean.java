package com.haipaite.common.scheduler.impl;

import javax.management.MXBean;

@MXBean
public interface SchedulerMonitorMBean {
  int getSchedulerQueueSize();
  
  int getPoolActiveCount();
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\SchedulerMonitorMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */