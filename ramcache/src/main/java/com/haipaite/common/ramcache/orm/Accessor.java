package com.haipaite.common.ramcache.orm;

import java.util.List;

public interface Accessor {
  <PK extends java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity> T load(Class<T> paramClass, PK paramPK);
  
  <PK extends java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity> PK save(Class<T> paramClass, T paramT);
  
  <PK extends java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity> void remove(Class<T> paramClass, PK paramPK);
  
  <PK extends java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity> void update(Class<T> paramClass, T paramT);
  
  <PK extends java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity> void batchSave(List<T> paramList);
  
  <PK extends java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity> void batchUpdate(List<T> paramList);
  
  <PK extends java.io.Serializable, T extends com.haipaite.common.ramcache.IEntity> void batchDelete(List<T> paramList);
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\orm\Accessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */