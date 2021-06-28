 package com.haipaite.common.utility;
 
 import java.security.MessageDigest;
 import java.security.SecureRandom;
 import javax.crypto.Cipher;
 import javax.crypto.SecretKey;
 import javax.crypto.SecretKeyFactory;
 import javax.crypto.spec.DESKeySpec;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class CryptUtils
 {
   private static final String PASSWORD_CRYPT_KEY = "@HaiPaiTe.COM";
   private static final String DES = "DES";
   
   public static byte[] md5(byte[] src) throws Exception {
     MessageDigest alg = MessageDigest.getInstance("MD5");
     return alg.digest(src);
   }
 
 
 
 
 
 
 
   
   public static String md5(String src) throws Exception {
     return byte2hex(md5(src.getBytes()));
   }
 
 
 
 
 
 
 
 
 
   
   public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
     SecureRandom sr = new SecureRandom();
     
     DESKeySpec dks = new DESKeySpec(key);
 
     
     SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
     SecretKey securekey = keyFactory.generateSecret(dks);
     
     Cipher cipher = Cipher.getInstance("DES");
     
     cipher.init(1, securekey, sr);
 
     
     return cipher.doFinal(src);
   }
   
   public static byte[] hex2byte(byte[] b) {
     if (b.length % 2 != 0)
       throw new IllegalArgumentException("长度不是偶数"); 
     byte[] b2 = new byte[b.length / 2];
     for (int n = 0; n < b.length; n += 2) {
       String item = new String(b, n, 2);
       b2[n / 2] = (byte)Integer.parseInt(item, 16);
     } 
     return b2;
   }
 
 
 
 
 
 
 
 
 
   
   public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
     SecureRandom sr = new SecureRandom();
     
     DESKeySpec dks = new DESKeySpec(key);
 
     
     SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
     SecretKey securekey = keyFactory.generateSecret(dks);
     
     Cipher cipher = Cipher.getInstance("DES");
     
     cipher.init(2, securekey, sr);
 
     
     return cipher.doFinal(src);
   }
 
 
 
 
 
 
 
   
   public static final String decryptPassword(String data) {
     if (data != null)
       try {
         return new String(decrypt(hex2byte(data.getBytes()), "@HaiPaiTe.COM"
               .getBytes()));
       } catch (Exception e) {
         e.printStackTrace();
       }  
     return null;
   }
 
 
 
 
 
 
 
   
   public static final String encryptPassword(String password) {
     if (password != null)
       try {
         return byte2hex(encrypt(password.getBytes(), "@HaiPaiTe.COM"
               .getBytes()));
       } catch (Exception e) {
         e.printStackTrace();
       }  
     return null;
   }
 
 
 
 
 
 
   
   public static String byte2hex(byte[] b) {
     StringBuilder sb = new StringBuilder();
     String stmp = "";
     for (int n = 0; b != null && n < b.length; n++) {
       stmp = Integer.toHexString(b[n] & 0xFF);
       if (stmp.length() == 1) {
         sb.append("0").append(stmp);
       } else {
         sb.append(stmp);
       } 
     }  return sb.toString().toUpperCase();
   }
 
 
 
 
 
 
   
   public static String byte2webhex(byte[] b) {
     return byte2hex(b, "%");
   }
 
 
 
 
 
 
 
   
   public static String byte2hex(byte[] b, String elide) {
     StringBuilder sb = new StringBuilder();
     String stmp = "";
     elide = (elide == null) ? "" : elide;
     for (int n = 0; b != null && n < b.length; n++) {
       stmp = Integer.toHexString(b[n] & 0xFF);
       if (stmp.length() == 1) {
         sb.append(elide).append("0").append(stmp);
       } else {
         sb.append(elide).append(stmp);
       } 
     }  return sb.toString().toUpperCase();
   }
   
   public static String getMD5(byte[] source) {
     String s = null;
     char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
 
     
     try {
       MessageDigest md = MessageDigest.getInstance("MD5");
       md.update(source);
       byte[] tmp = md.digest();
       char[] str = new char[32];
       int k = 0;
       for (int i = 0; i < 16; i++) {
         byte byte0 = tmp[i];
         str[k++] = hexDigits[byte0 >>> 4 & 0xF];
         str[k++] = hexDigits[byte0 & 0xF];
       } 
       s = new String(str);
     }
     catch (Exception e) {
       e.printStackTrace();
     } 
     return s;
   }
 
 
 
 
   
   private static final String[] UPPERCASE = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M" };
 
 
 
 
 
 
 
   
   private static final String[] LOWERCASE = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m" };
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\CryptUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */