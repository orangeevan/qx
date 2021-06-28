 package com.haipaite.common.utility;

 import java.io.UnsupportedEncodingException;
 import java.nio.ByteBuffer;
 import java.util.regex.Pattern;

 public final class CharCheckUtil
 {
   private static ThreadLocal<Pattern> NICKNAME_PATTERN = new ThreadLocal<Pattern>() {
       protected Pattern initialValue() {
         return Pattern.compile("^[一-龥a-zA-Z0-9]+$");
       }
     };

   public static boolean checkString(String str) {
     if (str == null || str.isEmpty()) {
       return false;
     }

     Pattern pattern = NICKNAME_PATTERN.get();
     return pattern.matcher(str.trim()).find();
   }

   public static String filterOffUtf8Mb4(String text) throws UnsupportedEncodingException {
     byte[] bytes = text.getBytes("utf-8");
     ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
     int i = 0;
     while (i < bytes.length) {
       short b = (short)bytes[i];
       if (b > 0) {
         buffer.put(bytes[i++]);
         continue;
       }
       b = (short)(b + 256);
       if ((b ^ 0xC0) >> 4 == 0) {
         buffer.put(bytes, i, 2);
         i += 2; continue;
       }  if ((b ^ 0xE0) >> 4 == 0) {
         buffer.put(bytes, i, 3);
         i += 3; continue;
       }  if ((b ^ 0xF0) >> 4 == 0) {
         i += 4;
         continue;
       }
       buffer.put(bytes[i++]);
     }



     buffer.flip();
     return new String(buffer.array(), "utf-8");
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\CharCheckUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */