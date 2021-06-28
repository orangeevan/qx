 package com.haipaite.common.utility;
 
 
 
 
 
 
 
 
 
 
 public class ByteUtils
 {
   public static final int intFromByte(byte[] array) {
     return intFromByte(array, 0);
   }
 
 
 
 
 
 
   
   public static final int intFromByte(byte[] array, int offset) {
     return array[offset] << 24 | (array[offset + 1] & 0xFF) << 16 | (array[offset + 2] & 0xFF) << 8 | array[offset + 3] & 0xFF;
   }
 
 
 
 
 
 
 
 
   
   public static final byte[] intToByte(int number) {
     return intToByte(number, new byte[4], 0);
   }
 
 
 
 
 
 
 
   
   public static final byte[] intToByte(int number, byte[] array, int offset) {
     array[offset + 3] = (byte)number;
     array[offset + 2] = (byte)(number >> 8);
     array[offset + 1] = (byte)(number >> 16);
     array[offset] = (byte)(number >> 24);
     return array;
   }
 
 
 
 
 
   
   public static long longFromByte(byte[] b) {
     return longFromByte(b, 0);
   }
 
 
 
 
 
 
   
   public static long longFromByte(byte[] array, int offset) {
     long s0 = (array[offset] & 0xFF);
     long s1 = (array[offset + 1] & 0xFF);
     long s2 = (array[offset + 2] & 0xFF);
     long s3 = (array[offset + 3] & 0xFF);
     long s4 = (array[offset + 4] & 0xFF);
     long s5 = (array[offset + 5] & 0xFF);
     long s6 = (array[offset + 6] & 0xFF);
     long s7 = (array[offset + 7] & 0xFF);
     return s0 << 56L | s1 << 48L | s2 << 40L | s3 << 32L | s4 << 24L | s5 << 16L | s6 << 8L | s7;
   }
 
 
 
 
 
   
   public static byte[] longToByte(long number) {
     return longToByte(number, new byte[8], 0);
   }
 
 
 
 
 
 
 
   
   public static final byte[] longToByte(long number, byte[] array, int offset) {
     array[offset + 7] = (byte)(int)number;
     array[offset + 6] = (byte)(int)(number >> 8L);
     array[offset + 5] = (byte)(int)(number >> 16L);
     array[offset + 4] = (byte)(int)(number >> 24L);
     array[offset + 3] = (byte)(int)(number >> 32L);
     array[offset + 2] = (byte)(int)(number >> 40L);
     array[offset + 1] = (byte)(int)(number >> 48L);
     array[offset] = (byte)(int)(number >> 56L);
     return array;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\ByteUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */