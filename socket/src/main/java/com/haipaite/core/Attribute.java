 package com.haipaite.core;
 
 
 
 
 
 
 
 
 
 
 public enum Attribute
 {
   IDENTITY("IDENTITY"),
 
 
 
   
   MANAGEMENT("MANAGEMENT");
   
   private final String name;
   
   public boolean hasAttr(Wsession session) {
     return session.getAttributes().containsKey(getName());
   }
   
   Attribute(String name) {
     this.name = name;
   }
   
   public String getName() {
     return this.name;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\core\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */