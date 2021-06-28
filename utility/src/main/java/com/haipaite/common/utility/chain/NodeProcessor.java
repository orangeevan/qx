package com.haipaite.common.utility.chain;

public interface NodeProcessor extends Comparable<NodeProcessor> {
  String getName();
  
  int getIndex();
  
  Way getWay();
  
  boolean in(Notice paramNotice);
  
  boolean out(Notice paramNotice);
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\chain\NodeProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */