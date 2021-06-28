package com.haipaite.common.ramcache.service;

public interface Filter<T extends com.haipaite.common.ramcache.IEntity> {
  boolean isExclude(T paramT);
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\service\Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */