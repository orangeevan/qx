package com.haipaite.common.ramcache.service;

import com.haipaite.common.ramcache.anno.CachedEntityConfig;

public interface EnhanceService<PK extends Comparable<PK> & java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity<PK>> {
  void writeBack(PK paramPK, T paramT);
  
  CachedEntityConfig getEntityConfig();
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\service\EnhanceService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */