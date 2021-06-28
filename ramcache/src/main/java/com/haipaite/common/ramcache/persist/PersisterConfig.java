 package com.haipaite.common.ramcache.persist;

 public class PersisterConfig
 {
   private final PersisterType type;
   private final String value;

   public PersisterConfig(PersisterType type, String value) {
     this.type = type;
     this.value = value;
   }

   public PersisterType getType() {
     return this.type;
   }

   public String getValue() {
     return this.value;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\persist\PersisterConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */