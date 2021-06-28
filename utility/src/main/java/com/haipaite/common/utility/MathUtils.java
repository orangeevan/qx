 package com.haipaite.common.utility;
 
 import java.math.BigDecimal;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class MathUtils
 {
   public static double round(double value, int scale) {
     BigDecimal b = new BigDecimal(Double.toString(value));
     BigDecimal one = new BigDecimal("1");
     return b.divide(one, scale, 4).doubleValue();
   }
 
 
 
 
 
 
 
   
   public static double roundDown(double v, int scale) {
     BigDecimal b = new BigDecimal(Double.toString(v));
     BigDecimal one = new BigDecimal("1");
     return b.divide(one, scale, 1).doubleValue();
   }
 
 
 
 
 
 
 
   
   public static double roundUp(double value, int scale) {
     BigDecimal b = new BigDecimal(Double.toString(value));
     BigDecimal one = new BigDecimal("1");
     return b.divide(one, scale, 0).doubleValue();
   }
 
 
 
 
 
 
 
 
   
   public static double divideAndRoundUp(double value1, double value2, int scale) {
     BigDecimal bd1 = new BigDecimal(value1);
     BigDecimal bd2 = new BigDecimal(value2);
     return bd1.divide(bd2, scale, 0).doubleValue();
   }
 
 
 
 
 
 
 
 
   
   public static double divideAndRoundDown(double value1, double value2, int scale) {
     BigDecimal bd1 = new BigDecimal(value1);
     BigDecimal bd2 = new BigDecimal(value2);
     return bd1.divide(bd2, scale, 1).doubleValue();
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\MathUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */