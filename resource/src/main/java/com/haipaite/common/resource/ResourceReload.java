 package com.haipaite.common.resource;

 import java.util.Observable;
 import java.util.Observer;






 public interface ResourceReload
   extends Observer
 {
   void reload();

   Class<?> getResourceClass();

   default void update(Observable o, Object arg) {
     if (!(o instanceof Storage)) {
       return;
     }
     Storage storage = (Storage)o;
     if (storage.getClz() != getResourceClass()) {
       return;
     }
     reload();
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-resource-1.0.1.jar!\com\haipaite\common\resource\ResourceReload.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */