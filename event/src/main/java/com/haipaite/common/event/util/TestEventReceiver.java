 package com.haipaite.common.event.util;

 import com.haipaite.common.event.anno.ReceiverAnno;
 import org.springframework.stereotype.Component;

 @Component
 public class TestEventReceiver
 {
   @ReceiverAnno
   public void receive(TestEvent event) {
     System.out.println(TestEventReceiver.class.getSimpleName() + " " +
         Thread.currentThread().getName() + " " + event.getClass());
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-event-1.0.1.jar!\com\haipaite\common\even\\util\TestEventReceiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */