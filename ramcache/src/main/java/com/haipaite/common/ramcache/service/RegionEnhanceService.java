package com.haipaite.common.ramcache.service;

public interface RegionEnhanceService<PK extends Comparable<PK> & java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity<PK>> extends EnhanceService<PK, T> {
  void changeIndexValue(String paramString, T paramT, Object paramObject);
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\service\RegionEnhanceService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */