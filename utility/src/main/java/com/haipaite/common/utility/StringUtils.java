 package com.haipaite.common.utility;
 
 
 
 
 public class StringUtils
 {
   public static boolean isBlank(String str) {
     int strLen;
     if (str == null || (strLen = str.length()) == 0) {
       return true;
     }
     for (int i = 0; i < strLen; i++) {
       if (!Character.isWhitespace(str.charAt(i))) {
         return false;
       }
     } 
     return true;
   }
   
   public static void main(String[] args) {
     String sign = "海派特#################深圳海派特光伏科技有限公司#############HPT_SMOK##07837bfeaad4c676addce97aeda7be9d61557212615&0";
     String s = Base64Coder.encodeString(sign);
     System.err.println("encode after: " + s);
     String s1 = Base64Coder.decodeString(s);
     System.err.println("decode after: " + s1);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\StringUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */