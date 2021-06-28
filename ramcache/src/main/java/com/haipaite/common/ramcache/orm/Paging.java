 package com.haipaite.common.ramcache.orm;









 public class Paging
 {
   private final int page;
   private final int size;

   public static Paging valueOf(int page, int size) {
     return new Paging(page, size);
   }












   public Paging(int page, int size) {
     if (page <= 0 || size <= 0) {
       throw new IllegalArgumentException("页码或页容量必须是大于或等于1的正整数");
     }
     this.page = page;
     this.size = size;
   }





   public int getFirst() {
     return this.size * this.page - this.size;
   }





   public int getLast() {
     return this.size * this.page;
   }







   public int getPage() {
     return this.page;
   }





   public int getSize() {
     return this.size;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\orm\Paging.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */