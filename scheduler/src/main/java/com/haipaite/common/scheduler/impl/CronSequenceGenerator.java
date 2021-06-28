 package com.haipaite.common.scheduler.impl;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.BitSet;
 import java.util.Calendar;
 import java.util.Collections;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.Iterator;
 import java.util.List;
 import java.util.TimeZone;
 import org.springframework.util.StringUtils;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class CronSequenceGenerator
 {
   private final BitSet seconds = new BitSet(60);
   
   private final BitSet minutes = new BitSet(60);
   
   private final BitSet hours = new BitSet(24);
   
   private final BitSet daysOfWeek = new BitSet(7);
   
   private final BitSet daysOfMonth = new BitSet(31);
   
   private final BitSet months = new BitSet(12);
 
 
   
   private final String expression;
 
 
   
   private final TimeZone timeZone;
 
 
   
   public CronSequenceGenerator(String expression, TimeZone timeZone) {
     this.expression = expression;
     this.timeZone = timeZone;
     parse(expression);
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public Date next(Date date) {
     Calendar calendar = new GregorianCalendar();
     calendar.setTimeZone(this.timeZone);
     calendar.setTime(date);
 
     
     calendar.add(13, 1);
     calendar.set(14, 0);
     
     doNext(calendar, calendar.get(1));
     
     return calendar.getTime();
   }
   
   private void doNext(Calendar calendar, int dot) {
     List<Integer> resets = new ArrayList<>();
     
     int second = calendar.get(13);
     List<Integer> emptyList = Collections.emptyList();
     int updateSecond = findNext(this.seconds, second, calendar, 13, 12, emptyList);
     if (second == updateSecond) {
       resets.add(Integer.valueOf(13));
     }
     
     int minute = calendar.get(12);
     int updateMinute = findNext(this.minutes, minute, calendar, 12, 11, resets);
     if (minute == updateMinute) {
       resets.add(Integer.valueOf(12));
     } else {
       doNext(calendar, dot);
     } 
     
     int hour = calendar.get(11);
     int updateHour = findNext(this.hours, hour, calendar, 11, 7, resets);
     if (hour == updateHour) {
       resets.add(Integer.valueOf(11));
     } else {
       doNext(calendar, dot);
     } 
     
     int dayOfWeek = calendar.get(7);
     int dayOfMonth = calendar.get(5);
     int updateDayOfMonth = findNextDay(calendar, this.daysOfMonth, dayOfMonth, this.daysOfWeek, dayOfWeek, resets);
     if (dayOfMonth == updateDayOfMonth) {
       resets.add(Integer.valueOf(5));
     } else {
       doNext(calendar, dot);
     } 
     
     int month = calendar.get(2);
     int updateMonth = findNext(this.months, month, calendar, 2, 1, resets);
     if (month != updateMonth) {
       if (calendar.get(1) - dot > 4) {
         throw new IllegalStateException("Invalid cron expression led to runaway search for next trigger");
       }
       doNext(calendar, dot);
     } 
   }
 
 
 
   
   private int findNextDay(Calendar calendar, BitSet daysOfMonth, int dayOfMonth, BitSet daysOfWeek, int dayOfWeek, List<Integer> resets) {
     int count = 0;
     int max = 366;
 
     
     while ((!daysOfMonth.get(dayOfMonth) || !daysOfWeek.get(dayOfWeek - 1)) && count++ < max) {
       calendar.add(5, 1);
       dayOfMonth = calendar.get(5);
       dayOfWeek = calendar.get(7);
       reset(calendar, resets);
     } 
     if (count >= max) {
       throw new IllegalStateException("Overflow in day for expression=" + this.expression);
     }
     return dayOfMonth;
   }
 
 
 
 
 
 
 
 
 
 
 
 
   
   private int findNext(BitSet bits, int value, Calendar calendar, int field, int nextField, List<Integer> lowerOrders) {
     int nextValue = bits.nextSetBit(value);
     
     if (nextValue == -1) {
       calendar.add(nextField, 1);
       reset(calendar, Arrays.asList(new Integer[] { Integer.valueOf(field) }));
       nextValue = bits.nextSetBit(0);
     } 
     if (nextValue != value) {
       calendar.set(field, nextValue);
       reset(calendar, lowerOrders);
     } 
     return nextValue;
   }
 
 
 
   
   private void reset(Calendar calendar, List<Integer> fields) {
     for (Iterator<Integer> iterator = fields.iterator(); iterator.hasNext(); ) { int field = ((Integer)iterator.next()).intValue();
       calendar.set(field, (field == 5) ? 1 : 0); }
   
   }
 
 
 
 
 
   
   private void parse(String expression) throws IllegalArgumentException {
     String[] fields = StringUtils.tokenizeToStringArray(expression, " ");
     if (fields.length != 6)
       throw new IllegalArgumentException(String.format("cron expression must consist of 6 fields (found %d in %s)", new Object[] {
               Integer.valueOf(fields.length), expression
             })); 
     setNumberHits(this.seconds, fields[0], 0, 60);
     setNumberHits(this.minutes, fields[1], 0, 60);
     setNumberHits(this.hours, fields[2], 0, 24);
     setDaysOfMonth(this.daysOfMonth, fields[3]);
     setMonths(this.months, fields[4]);
     setDays(this.daysOfWeek, replaceOrdinals(fields[5], "SUN,MON,TUE,WED,THU,FRI,SAT"), 8);
     if (this.daysOfWeek.get(7)) {
       
       this.daysOfWeek.set(0);
       this.daysOfWeek.clear(7);
     } 
   }
 
 
 
 
 
   
   private String replaceOrdinals(String value, String commaSeparatedList) {
     String[] list = StringUtils.commaDelimitedListToStringArray(commaSeparatedList);
     for (int i = 0; i < list.length; i++) {
       String item = list[i].toUpperCase();
       value = StringUtils.replace(value.toUpperCase(), item, "" + i);
     } 
     return value;
   }
   
   private void setDaysOfMonth(BitSet bits, String field) {
     int max = 31;
     
     setDays(bits, field, max + 1);
     
     bits.clear(0);
   }
   
   private void setDays(BitSet bits, String field, int max) {
     if (field.contains("?")) {
       field = "*";
     }
     setNumberHits(bits, field, 0, max);
   }
   
   private void setMonths(BitSet bits, String value) {
     int max = 12;
     value = replaceOrdinals(value, "FOO,JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC");
     BitSet months = new BitSet(13);
     
     setNumberHits(months, value, 1, max + 1);
     
     for (int i = 1; i <= max; i++) {
       if (months.get(i)) {
         bits.set(i - 1);
       }
     } 
   }
   
   private void setNumberHits(BitSet bits, String value, int min, int max) {
     String[] fields = StringUtils.delimitedListToStringArray(value, ",");
     for (String field : fields) {
       if (!field.contains("/")) {
         
         int[] range = getRange(field, min, max);
         bits.set(range[0], range[1] + 1);
       } else {
         String[] split = StringUtils.delimitedListToStringArray(field, "/");
         if (split.length > 2) {
           throw new IllegalArgumentException("Incrementer has more than two fields: " + field);
         }
         int[] range = getRange(split[0], min, max);
         if (!split[0].contains("-")) {
           range[1] = max - 1;
         }
         int delta = Integer.valueOf(split[1]).intValue(); int i;
         for (i = range[0]; i <= range[1]; i += delta) {
           bits.set(i);
         }
       } 
     } 
   }
   
   private int[] getRange(String field, int min, int max) {
     int[] result = new int[2];
     if (field.contains("*")) {
       result[0] = min;
       result[1] = max - 1;
       return result;
     } 
     if (!field.contains("-")) {
       result[1] = Integer.valueOf(field).intValue(); result[0] = Integer.valueOf(field).intValue();
     } else {
       String[] split = StringUtils.delimitedListToStringArray(field, "-");
       if (split.length > 2) {
         throw new IllegalArgumentException("Range has more than two fields: " + field);
       }
       result[0] = Integer.valueOf(split[0]).intValue();
       result[1] = Integer.valueOf(split[1]).intValue();
     } 
     if (result[0] >= max || result[1] >= max) {
       throw new IllegalArgumentException("Range exceeds maximum (" + max + "): " + field);
     }
     if (result[0] < min || result[1] < min) {
       throw new IllegalArgumentException("Range less than minimum (" + min + "): " + field);
     }
     return result;
   }
 
   
   public boolean equals(Object obj) {
     if (!(obj instanceof CronSequenceGenerator)) {
       return false;
     }
     CronSequenceGenerator cron = (CronSequenceGenerator)obj;
     return (cron.months.equals(this.months) && cron.daysOfMonth.equals(this.daysOfMonth) && cron.daysOfWeek
       .equals(this.daysOfWeek) && cron.hours.equals(this.hours) && cron.minutes
       .equals(this.minutes) && cron.seconds.equals(this.seconds));
   }
 
   
   public int hashCode() {
     return 37 + 17 * this.months.hashCode() + 29 * this.daysOfMonth.hashCode() + 37 * this.daysOfWeek.hashCode() + 41 * this.hours
       .hashCode() + 53 * this.minutes.hashCode() + 61 * this.seconds.hashCode();
   }
 
   
   public String toString() {
     return getClass().getSimpleName() + ": " + this.expression;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-scheduler-1.0.1.jar!\com\haipaite\common\scheduler\impl\CronSequenceGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */