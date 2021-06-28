package com.haipaite.common.ramcache.service;

import com.haipaite.common.ramcache.anno.CachedEntityConfig;
import com.haipaite.common.ramcache.persist.Persister;
import java.util.Collection;

public interface RegionCacheService<PK extends Comparable<PK> & java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity<PK>> {
  Collection<T> load(IndexValue paramIndexValue);
  
  T get(IndexValue paramIndexValue, PK paramPK);
  
  T create(T paramT);
  
  void remove(T paramT);
  
  void clear(IndexValue paramIndexValue);
  
  void writeBack(PK paramPK, T paramT);
  
  CachedEntityConfig getEntityConfig();
  
  Persister getPersister();
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\service\RegionCacheService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */