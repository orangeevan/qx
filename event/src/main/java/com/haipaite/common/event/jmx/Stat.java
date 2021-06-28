 package com.haipaite.common.event.jmx;

 import com.haipaite.common.event.event.IEvent;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.TreeSet;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 import java.util.concurrent.atomic.AtomicLong;


 public class Stat
 {
   private AtomicLong totalPackets = new AtomicLong();
   private AtomicLong totalTimes = new AtomicLong();

   private ConcurrentMap<Class<? extends IEvent>, EventStat> eventStatMap = new ConcurrentHashMap<>();

   public AtomicLong getTotalPackets() {
     return this.totalPackets;
   }

   public void setTotalPackets(AtomicLong totalPackets) {
     this.totalPackets = totalPackets;
   }

   public AtomicLong getTotalTimes() {
     return this.totalTimes;
   }

   public void setTotalTimes(AtomicLong totalTimes) {
     this.totalTimes = totalTimes;
   }

   public ConcurrentMap<Class<? extends IEvent>, EventStat> getEventStatMap() {
     return this.eventStatMap;
   }

   public void setEventStatMap(ConcurrentMap<Class<? extends IEvent>, EventStat> eventStatMap) {
     this.eventStatMap = eventStatMap;
   }

   public void addEvent(Class<? extends IEvent> clz, long use, boolean over) {
     getEventStat(clz).add(use, over);
     this.totalPackets.incrementAndGet();
     this.totalTimes.addAndGet(use);
   }

   private EventStat getEventStat(Class<? extends IEvent> clz) {
     EventStat stat = this.eventStatMap.get(clz);
     if (stat == null) {
       stat = new EventStat(clz.getName());
       EventStat pre = this.eventStatMap.put(clz, stat);
       if (pre != null) {
         stat = pre;
       }
     }
     return stat;
   }

   public String[] getEventInfo() {
     List<String> result = new LinkedList<>();
     long totalPacket = this.totalPackets.get();
     long totalTime = this.totalTimes.get() / 1000000L;
     result.add("totalPacket : " + totalPacket);
     result.add("totalTime : " + totalTime);

     TreeSet<EventStat> set1 = new TreeSet<>();

     for (Map.Entry<Class<? extends IEvent>, EventStat> entry : this.eventStatMap.entrySet()) {
       set1.add(entry.getValue());
     }

     for (EventStat stat : set1) {
       result.add(stat.toString());
     }

     return result.<String>toArray(new String[result.size()]);
   }

   public class EventStat implements Comparable<EventStat> {
     private final AtomicLong eventTimes = new AtomicLong();
     private final AtomicLong eventTotalTime = new AtomicLong();
     private final AtomicLong eventOverTimes = new AtomicLong();

     private final String className;

     public EventStat(String className) {
       this.className = className;
     }

     public void add(long use, boolean over) {
       this.eventTimes.incrementAndGet();
       this.eventTotalTime.addAndGet(use);
       if (over) {
         this.eventOverTimes.incrementAndGet();
       }
     }

     public String toString() {
       long totalPacket = Stat.this.totalPackets.get();
       long packetTimes = this.eventTimes.get();
       long eventTotalTimes = this.eventTotalTime.get() / 1000000L;

       float packetTimeOpps = (float)packetTimes * 1.0F / (float)totalPacket * 100.0F;
       float averageTime = (float)eventTotalTimes * 1.0F / (float)packetTimes;
       long overTime = this.eventOverTimes.get();

       return
         String.format("[name : %s]  [packetTimes : %d] [packetOpps : %02.2f%%] [averageTime : %02.2fms] [totalTimes : %dms] [overTime : %d]", new Object[] {
             this.className, Long.valueOf(packetTimes), Float.valueOf(packetTimeOpps), Float.valueOf(averageTime), Long.valueOf(eventTotalTimes), Long.valueOf(overTime)
           });
     }

     public int compareTo(EventStat o) {
       long result = o.eventTotalTime.get() - this.eventTotalTime.get();
       if (result > 0L)
         return 1;
       if (result < 0L) {
         return -1;
       }
       return o.hashCode() - hashCode();
     }


     public AtomicLong getEventTimes() {
       return this.eventTimes;
     }

     public AtomicLong getEventTotalTime() {
       return this.eventTotalTime;
     }

     public AtomicLong getEventOverTimes() {
       return this.eventOverTimes;
     }

     public String getClassName() {
       return this.className;
     }
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-event-1.0.1.jar!\com\haipaite\common\event\jmx\Stat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */