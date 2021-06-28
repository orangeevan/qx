 package com.haipaite.common.utility.chain;
 
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 public class ProcessChain
 {
   private NodeProcessor current;
   private ProcessChain next;
   
   public ProcessChain(List<NodeProcessor> nodes) {
     if (nodes == null || nodes.size() == 0) {
       throw new IllegalArgumentException("处理链的节点数量不能为0");
     }
     this.current = nodes.remove(0);
     if (nodes.size() > 0) {
       this.next = new ProcessChain(nodes);
     } else {
       this.next = null;
     } 
   }
 
 
 
 
 
 
   
   public boolean process(Notice notice) {
     int step = notice.getStep();
     step++;
     notice.setStep(step);
     notice.setWay(Way.IN);
     if (!this.current.in(notice)) {
       return false;
     }
     if (this.next != null && 
       !this.next.process(notice)) {
       return false;
     }
     
     if (!this.current.out(notice)) {
       return false;
     }
     step--;
     notice.setStep(step);
     notice.setWay(Way.OUT);
     return true;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\chain\ProcessChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */