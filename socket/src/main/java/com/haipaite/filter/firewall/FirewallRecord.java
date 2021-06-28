 package com.haipaite.filter.firewall;
 
 
 
 
 
 
 
 
 
 public class FirewallRecord
 {
   private static int MAX_VIOLATE_TIMES = 5;
   
   private static int BYTES_IN_SECOND_LIMIT = 40960;
   
   private static int TIMES_IN_SECOND_LIMIT = 128;
 
 
 
 
 
   
   public static void setMaxViolateTimes(int times) {
     MAX_VIOLATE_TIMES = times;
   }
 
 
 
 
 
   
   public static void setBytesInSecondLimit(int size) {
     BYTES_IN_SECOND_LIMIT = size;
   }
 
 
 
 
 
   
   public static void setTimesInSecondLimit(int size) {
     TIMES_IN_SECOND_LIMIT = size;
   }
 
   
   private long lastSecond = 0L;
   
   private int bytesInSecond = 0;
   
   private int timesInSecond = 0;
 
   
   private int violateTime = 0;
 
   
   public String toString() {
     StringBuilder str = new StringBuilder();
     str.append("{bytesInSecond:" + this.bytesInSecond + ",timesInSecond:" + this.timesInSecond + ",violateTime:" + this.violateTime + "}");
     
     return str.toString();
   }
 
 
 
 
 
 
 
 
   
   public boolean check(int bytes) {
     long ms = System.currentTimeMillis();
     
     long currentSecond = ms / 1000L;
     
     if (currentSecond != this.lastSecond) {
       
       this.lastSecond = currentSecond;
       this.bytesInSecond = 0;
       this.timesInSecond = 0;
     } 
 
     
     this.bytesInSecond += bytes;
     this.timesInSecond++;
 
 
     
     if (this.timesInSecond >= TIMES_IN_SECOND_LIMIT) {
       this.violateTime++;
       return true;
     } 
     System.err.println("流量包字节大小： " + this.bytesInSecond);
     return false;
   }
 
 
 
 
 
   
   public boolean isBlock() {
     return (this.violateTime >= MAX_VIOLATE_TIMES);
   }
 
 
   
   public long getLastSecond() {
     return this.lastSecond;
   }
   
   public int getBytesInSecond() {
     return this.bytesInSecond;
   }
   
   public int getTimesInSecond() {
     return this.timesInSecond;
   }
   
   public int getViolateTime() {
     return this.violateTime;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\filter\firewall\FirewallRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */