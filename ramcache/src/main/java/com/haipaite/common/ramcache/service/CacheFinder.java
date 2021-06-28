package com.haipaite.common.ramcache.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public interface CacheFinder<PK extends Comparable<PK> & java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity<PK>> {
  Set<T> find(Filter<T> paramFilter);
  
  List<T> sort(Comparator<T> paramComparator);
  
  List<T> find(Filter<T> paramFilter, Comparator<T> paramComparator);
  
  Set<T> all();
  
  int getAllSize();
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\service\CacheFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */