package com.haipaite.common.scheduler;

import java.util.Date;

public interface TaskContext {
  Date lastScheduledTime();
  
  Date lastActualTime();
  
  Date lastCompletionTime();
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\TaskContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */