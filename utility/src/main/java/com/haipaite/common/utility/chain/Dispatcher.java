 package com.haipaite.common.utility.chain;
 
 import java.util.HashMap;
 import java.util.Map;
 import org.springframework.stereotype.Component;
 
 
 
 
 
 
 @Component
 public class Dispatcher
 {
   private Map<String, ProcessChain> chains = new HashMap<>();
 
 
 
 
 
   
   public void process(Notice<?> notice) {
     String name = notice.getName();
     ProcessChain chain = this.chains.get(name);
     if (chain != null && 
       !chain.process(notice)) {
       notice.setInterrupt(true);
     }
   }
 
 
 
 
 
 
 
   
   public ProcessChain register(String name, ProcessChain chain) {
     return this.chains.put(name, chain);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\chain\Dispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */