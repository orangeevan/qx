package com.haipaite.common.ramcache.persist;

import com.haipaite.common.ramcache.IEntity;
import java.io.Serializable;

public interface Listener {
  void notify(EventType paramEventType, boolean paramBoolean, Serializable paramSerializable, IEntity paramIEntity, RuntimeException paramRuntimeException);
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\persist\Listener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */