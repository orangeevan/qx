package com.haipaite.common.ramcache.orm;

import java.util.List;

public interface Querier {
  <T> List<T> all(Class<T> paramClass);
  
  <T> List<T> list(Class<T> paramClass, String paramString, Object... paramVarArgs);
  
  <E> List<E> list(Class paramClass, Class<E> paramClass1, String paramString, Object... paramVarArgs);
  
  <T> T unique(Class<T> paramClass, String paramString, Object... paramVarArgs);
  
  <E> E unique(Class paramClass, Class<E> paramClass1, String paramString, Object... paramVarArgs);
  
  <T> List<T> paging(Class<T> paramClass, String paramString, Paging paramPaging, Object... paramVarArgs);
  
  <E> List<E> paging(Class paramClass, Class<E> paramClass1, String paramString, Paging paramPaging, Object... paramVarArgs);
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\orm\Querier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */