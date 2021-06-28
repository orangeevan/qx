 package com.haipaite.common.utility.chain;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 public class ChainBuilder
 {
   private List<NodeProcessor> processors = new ArrayList<>();
 
 
 
 
   
   public void addNode(NodeProcessor processor) {
     this.processors.add(processor);
   }
 
 
 
 
   
   public ProcessChain build() {
     Collections.sort(this.processors);
     return new ProcessChain(this.processors);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\chain\ChainBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */