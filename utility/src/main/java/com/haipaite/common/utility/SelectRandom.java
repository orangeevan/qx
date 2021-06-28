 package com.haipaite.common.utility;

 import java.util.ArrayList;
 import java.util.List;
 import org.apache.commons.lang.math.RandomUtils;









 public class SelectRandom<T>
 {
   private List<Element<T>> elements = new ArrayList<>();



   private int cusor = 0;

   public int size() {
     return this.elements.size();
   }










   public int addElement(T value, int pro) {
     Element<T> e = new Element<>();
     e.setValue(value);
     e.setProButtom(this.cusor);
     e.setProTop(this.cusor + pro);
     this.cusor += pro;
     this.elements.add(e);
     return this.elements.size();
   }




   public void clear() {
     this.elements.clear();
     this.cusor = 0;
   }







   public T run() {
     long rLong = RandomUtils.nextLong() % this.cusor;

     for (Element<T> element : this.elements) {
       if (element.getProTop() > rLong && element.getProButtom() <= rLong) {
         return element.getValue();
       }
     }
     return null;
   }







   public List<T> run(int count) {
     List<T> selects = new ArrayList<>();
     if (count >= this.elements.size()) {
       for (Element<T> ele : this.elements) {
         selects.add(ele.getValue());
       }
       this.elements.clear();
       this.cusor = 0;
       return selects;
     }

     while (selects.size() < count) {
       T s = run();
       remove(s);

       selects.add(s);
     }


     return selects;
   }







   public List<T> run(int count, boolean disorder) {
     List<T> selects = new ArrayList<>();
     int sc = count;
     if (!disorder) {
       return run(count);
     }

     if (count > this.elements.size() && disorder) {
       sc = this.elements.size();
     }

     while (selects.size() < sc) {
       T s = run();
       remove(s);
       selects.add(s);
     }
     return selects;
   }


   public SelectRandom<T> clone() {
     SelectRandom<T> selects = new SelectRandom();
     for (Element<T> ele : this.elements) {
       selects.addElement(ele.getValue(), ele.getProTop() - ele.getProButtom());
     }
     return selects;
   }

   private void remove(T s) {
     Element<T> e = null;
     for (Element<T> ele : this.elements) {
       if (ele.getValue() == s) {
         e = ele;
         break;
       }
     }
     if (e == null) {
       System.out.println();
     }
     int range = e.getProTop() - e.getProButtom();
     boolean flag = false;
     for (Element<T> ele : this.elements) {
       if (flag) {
         ele.setProButtom(ele.getProButtom() - range);
         ele.setProTop(ele.getProTop() - range);
       }
       if (ele == e) {
         flag = true;
       }
     }
     this.cusor -= range;
     this.elements.remove(e);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\SelectRandom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */