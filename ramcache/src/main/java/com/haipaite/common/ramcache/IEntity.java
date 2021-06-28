package com.haipaite.common.ramcache;

public interface IEntity<PK extends java.io.Serializable & Comparable<PK>> {
  PK getId();
  
  boolean serialize();
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\IEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */