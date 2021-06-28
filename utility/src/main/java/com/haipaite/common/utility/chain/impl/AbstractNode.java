 package com.haipaite.common.utility.chain.impl;

 import com.haipaite.common.utility.chain.NodeProcessor;
 import com.haipaite.common.utility.chain.Notice;
 import com.haipaite.common.utility.chain.Way;






 public abstract class AbstractNode
   implements NodeProcessor
 {
   private final String name;
   private final int index;
   private final Way way;

   public AbstractNode(String name, int index, Way way) {
     this.name = name;
     this.index = index;
     this.way = way;
   }


   public String getName() {
     return this.name;
   }


   public int getIndex() {
     return this.index;
   }

   public Way getWay() {
     return this.way;
   }


   public int compareTo(NodeProcessor o) {
     if (getIndex() > o.getIndex())
       return 1;
     if (getIndex() < o.getIndex()) {
       return -1;
     }
     return 0;
   }


   public boolean in(Notice notice) {
     return true;
   }


   public boolean out(Notice notice) {
     return true;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\chain\impl\AbstractNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */