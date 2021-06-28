 package com.haipaite.common.utility;

 import java.sql.Date;


 public class NumberUtils
 {
   public static <T> T valueOf(Class<T> resultType, Number value) {
     if (resultType == null) {
       String str = value.getClass().getSimpleName() + " -> NULL";
       throw new NullPointerException(str);
     }
     if (resultType == Date.class)
       return (T)new Date(value.longValue());
     if (resultType == int.class || resultType == Integer.class)
       return (T)Integer.valueOf(value.intValue());
     if (resultType == double.class || resultType == Double.class)
       return (T)value;
     if (resultType == boolean.class || resultType == Boolean.class)
       return (T)Boolean.valueOf((value.intValue() == 0));
     if (resultType == byte.class || resultType == Byte.class)
       return (T)Byte.valueOf(value.byteValue());
     if (resultType == long.class || resultType == Long.class)
       return (T)Long.valueOf(value.longValue());
     if (resultType == short.class || resultType == Short.class)
       return (T)Short.valueOf(value.shortValue());
     if (resultType == float.class || resultType == Float.class)
       return (T)Float.valueOf(value.floatValue());
     if (resultType == Number.class) {
       return (T)value;
     }

     String msg = value.getClass().getSimpleName() + " -> " + resultType.getSimpleName();
     throw new IllegalArgumentException(new ClassCastException(msg));
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\NumberUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */