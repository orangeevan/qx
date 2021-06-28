package com.haipaite.common.ramcache.service;

import com.haipaite.common.ramcache.anno.CachedEntityConfig;
import com.haipaite.common.ramcache.orm.Accessor;
import com.haipaite.common.ramcache.orm.Querier;
import com.haipaite.common.ramcache.persist.Persister;

public interface EntityCacheService<PK extends Comparable<PK> & java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity<PK>> {
  T load(PK paramPK);
  
  T unique(String paramString, Object paramObject);
  
  T loadOrCreate(PK paramPK, EntityBuilder<PK, T> paramEntityBuilder);
  
  T create(PK paramPK, EntityBuilder<PK, T> paramEntityBuilder);
  
  void writeBack(PK paramPK, T paramT);
  
  T remove(PK paramPK);
  
  void clear(PK paramPK);
  
  CacheFinder<PK, T> getFinder();
  
  CachedEntityConfig getEntityConfig();
  
  boolean hasUniqueValue(String paramString, Object paramObject);
  
  Persister getPersister();
  
  void initialize(CachedEntityConfig paramCachedEntityConfig, Persister paramPersister, Accessor paramAccessor, Querier paramQuerier);
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\service\EntityCacheService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */