 package com.haipaite.common.utility;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 class Element<T>
 {
   private T value;
   private int proTop;
   private int proButtom;
   
   public void setValue(T value) {
     this.value = value;
   }
   
   public T getValue() {
     return this.value;
   }
   
   public void setProTop(int proTop) {
     this.proTop = proTop;
   }
   
   public int getProTop() {
     return this.proTop;
   }
   
   public void setProButtom(int proButtom) {
     this.proButtom = proButtom;
   }
   
   public int getProButtom() {
     return this.proButtom;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */