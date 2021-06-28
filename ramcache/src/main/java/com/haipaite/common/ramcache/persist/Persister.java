package com.haipaite.common.ramcache.persist;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.orm.Accessor;
import java.util.Map;

public interface Persister {
  void initialize(String paramString1, Accessor paramAccessor, String paramString2);
  
  void put(Element paramElement);
  
  void addListener(Class<? extends IEntity> paramClass, Listener paramListener);
  
  Listener getListener(Class<? extends IEntity> paramClass);
  
  void shutdown();
  
  Map<String, String> getInfo();
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\persist\Persister.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */