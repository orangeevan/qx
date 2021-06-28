 package com.haipaite.common.utility;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.concurrent.ConcurrentHashMap;
 
 
 
 
 
 
 
 
 
 
 
 
 public class New
 {
   public static <T> ArrayList<T> arrayList() {
     return new ArrayList<>(4);
   }
 
 
 
 
 
 
 
 
 
   
   public static <K, V> HashMap<K, V> hashMap() {
     return new HashMap<>();
   }
 
 
 
 
 
 
 
 
 
 
 
   
   public static <K, V> HashMap<K, V> hashMap(int initialCapacity) {
     return new HashMap<>(initialCapacity);
   }
   
   public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap() {
     return new ConcurrentHashMap<>();
   }
 
 
 
 
 
 
 
   
   public static <T> HashSet<T> hashSet() {
     return new HashSet<>();
   }
 
 
 
 
 
 
 
 
 
   
   public static <T> ArrayList<T> arrayList(Collection<T> c) {
     return new ArrayList<>(c);
   }
 
 
 
 
 
 
 
 
 
   
   public static <T> ArrayList<T> arrayList(int initialCapacity) {
     return new ArrayList<>(initialCapacity);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\New.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */