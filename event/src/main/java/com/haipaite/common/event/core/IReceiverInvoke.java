package com.haipaite.common.event.core;

import com.haipaite.common.event.event.IEvent;

public interface IReceiverInvoke {
  Object getBean();
  
  void invoke(IEvent paramIEvent);
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-event-1.0.1.jar!\com\haipaite\common\event\core\IReceiverInvoke.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */