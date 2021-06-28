 package com.haipaite.common.utility.collection;

 import java.io.Serializable;
 import java.util.AbstractSet;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.concurrent.ConcurrentHashMap;










 public class ConcurrentHashSet<E>
   extends AbstractSet<E>
   implements Serializable
 {
   private static final long serialVersionUID = -807744025477162175L;
   protected final ConcurrentHashMap<E, Boolean> map;

   public ConcurrentHashSet() {
     this.map = new ConcurrentHashMap<>();
   }






   public ConcurrentHashSet(int initialCapacity) {
     this.map = new ConcurrentHashMap<>(initialCapacity);
   }







   public ConcurrentHashSet(int initialCapacity, float loadFactor) {
     this.map = new ConcurrentHashMap<>(initialCapacity, loadFactor);
   }








   public ConcurrentHashSet(int initialCapacity, float loadFactor, int concurrencyLevel) {
     this.map = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
   }





   public ConcurrentHashSet(Collection<E> collection) {
     this(collection.size());
     addAll(collection);
   }


   public int size() {
     return this.map.size();
   }


   public boolean contains(Object o) {
     return this.map.containsKey(o);
   }


   public Iterator<E> iterator() {
     return this.map.keySet().iterator();
   }


   public boolean add(E o) {
     return (this.map.put(o, Boolean.TRUE) == null);
   }


   public boolean remove(Object o) {
     return (this.map.remove(o) != null);
   }


   public void clear() {
     this.map.clear();
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\collection\ConcurrentHashSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */