 package com.haipaite.common.utility.chain;











 public class Notice<T>
 {
   private final String name;
   private final T content;
   private int step;
   private Way way;
   private boolean interrupt;

   public static <T> Notice<T> valueOf(String name, T content) {
     return new Notice<>(name, content);
   }

   public Notice(String name, T content) {
     this.name = name;
     this.content = content;
   }





   public String getName() {
     return this.name;
   }





   public T getContent() {
     return this.content;
   }





   public int getStep() {
     return this.step;
   }





   void setStep(int step) {
     this.step = step;
   }





   public Way getWay() {
     return this.way;
   }





   void setWay(Way way) {
     this.way = way;
   }





   public boolean isInterrupt() {
     return this.interrupt;
   }





   void setInterrupt(boolean interrupt) {
     this.interrupt = interrupt;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\chain\Notice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */