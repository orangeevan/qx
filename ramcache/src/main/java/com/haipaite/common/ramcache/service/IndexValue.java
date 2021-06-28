 package com.haipaite.common.ramcache.service;
 
 
 
 
 
 
 
 
 public class IndexValue
 {
   private final String name;
   private final Object value;
   
   public static IndexValue valueOf(String name, Object value) {
     if (name == null) {
       throw new IllegalArgumentException("索引名不能为null");
     }
     return new IndexValue(name, value);
   }
 
 
 
 
   
   private IndexValue(String name, Object value) {
     this.name = name;
     this.value = value;
   }
 
 
 
 
   
   public String getName() {
     return this.name;
   }
 
 
 
 
   
   public Object getValue() {
     return this.value;
   }
 
 
 
 
 
   
   public <T> T getValue(Class<T> clz) {
     return (T)this.value;
   }
 
   
   public int hashCode() {
     int prime = 31;
     int result = 1;
     result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
     result = 31 * result + ((this.value == null) ? 0 : this.value.hashCode());
     return result;
   }
 
   
   public boolean equals(Object obj) {
     if (this == obj)
       return true; 
     if (obj == null)
       return false; 
     if (getClass() != obj.getClass())
       return false; 
     IndexValue other = (IndexValue)obj;
     if (this.name == null) {
       if (other.name != null)
         return false; 
     } else if (!this.name.equals(other.name)) {
       return false;
     }  if (this.value == null) {
       if (other.value != null)
         return false; 
     } else if (!this.value.equals(other.value)) {
       return false;
     }  return true;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\service\IndexValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */