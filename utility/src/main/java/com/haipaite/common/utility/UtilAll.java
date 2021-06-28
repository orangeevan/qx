 package com.haipaite.common.utility;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.lang.management.ManagementFactory;
 import java.lang.management.RuntimeMXBean;
 import java.net.InetAddress;
 import java.net.NetworkInterface;
 import java.text.NumberFormat;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Enumeration;
 import java.util.zip.CRC32;
 import java.util.zip.Deflater;
 import java.util.zip.DeflaterOutputStream;
 import java.util.zip.InflaterInputStream;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class UtilAll
 {
   public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
   public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd#HH:mm:ss:SSS";
   public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
   static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
   
   public static int getPid() {
     RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
     String name = runtime.getName();
     try {
       return Integer.parseInt(name.substring(0, name.indexOf('@')));
     } catch (Exception e) {
       return -1;
     } 
   }
   
   public static void sleep(long sleepMs) {
     if (sleepMs < 0L) {
       return;
     }
     try {
       Thread.sleep(sleepMs);
     } catch (Throwable throwable) {}
   }
 
 
 
   
   public static String currentStackTrace() {
     StringBuilder sb = new StringBuilder();
     StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
     for (StackTraceElement ste : stackTrace) {
       sb.append("\n\t");
       sb.append(ste.toString());
     } 
     
     return sb.toString();
   }
   
   public static String offset2FileName(long offset) {
     NumberFormat nf = NumberFormat.getInstance();
     nf.setMinimumIntegerDigits(20);
     nf.setMaximumFractionDigits(0);
     nf.setGroupingUsed(false);
     return nf.format(offset);
   }
   
   public static long computeEclipseTimeMilliseconds(long beginTime) {
     return System.currentTimeMillis() - beginTime;
   }
   
   public static boolean isItTimeToDo(String when) {
     String[] whiles = when.split(";");
     if (whiles.length > 0) {
       Calendar now = Calendar.getInstance();
       for (String w : whiles) {
         int nowHour = Integer.parseInt(w);
         if (nowHour == now.get(11)) {
           return true;
         }
       } 
     } 
     
     return false;
   }
   
   public static String timeMillisToHumanString() {
     return timeMillisToHumanString(System.currentTimeMillis());
   }
   
   public static String timeMillisToHumanString(long t) {
     Calendar cal = Calendar.getInstance();
     cal.setTimeInMillis(t);
     return String.format("%04d%02d%02d%02d%02d%02d%03d", new Object[] { Integer.valueOf(cal.get(1)), Integer.valueOf(cal.get(2) + 1), 
           Integer.valueOf(cal.get(5)), Integer.valueOf(cal.get(11)), Integer.valueOf(cal.get(12)), Integer.valueOf(cal.get(13)), 
           Integer.valueOf(cal.get(14)) });
   }
   
   public static void main(String[] args) {
     System.err.println(timeMillisToHumanString(System.currentTimeMillis()));
     System.err.println(timeMillisToHumanString2(System.currentTimeMillis()));
     System.err.println(timeMillisToHumanString3(System.currentTimeMillis()));
   }
 
   
   public static long computNextMorningTimeMillis() {
     Calendar cal = Calendar.getInstance();
     cal.setTimeInMillis(System.currentTimeMillis());
     cal.add(5, 1);
     cal.set(11, 0);
     cal.set(12, 0);
     cal.set(13, 0);
     cal.set(14, 0);
     
     return cal.getTimeInMillis();
   }
   
   public static long computNextMinutesTimeMillis() {
     Calendar cal = Calendar.getInstance();
     cal.setTimeInMillis(System.currentTimeMillis());
     cal.add(5, 0);
     cal.add(11, 0);
     cal.add(12, 1);
     cal.set(13, 0);
     cal.set(14, 0);
     
     return cal.getTimeInMillis();
   }
   
   public static long computNextHourTimeMillis() {
     Calendar cal = Calendar.getInstance();
     cal.setTimeInMillis(System.currentTimeMillis());
     cal.add(5, 0);
     cal.add(11, 1);
     cal.set(12, 0);
     cal.set(13, 0);
     cal.set(14, 0);
     
     return cal.getTimeInMillis();
   }
   
   public static long computNextHalfHourTimeMillis() {
     Calendar cal = Calendar.getInstance();
     cal.setTimeInMillis(System.currentTimeMillis());
     cal.add(5, 0);
     cal.add(11, 1);
     cal.set(12, 30);
     cal.set(13, 0);
     cal.set(14, 0);
     
     return cal.getTimeInMillis();
   }
   
   public static String timeMillisToHumanString2(long t) {
     Calendar cal = Calendar.getInstance();
     cal.setTimeInMillis(t);
     return String.format("%04d-%02d-%02d %02d:%02d:%02d,%03d", new Object[] {
           Integer.valueOf(cal.get(1)), 
           Integer.valueOf(cal.get(2) + 1), 
           Integer.valueOf(cal.get(5)), 
           Integer.valueOf(cal.get(11)), 
           Integer.valueOf(cal.get(12)), 
           Integer.valueOf(cal.get(13)), 
           Integer.valueOf(cal.get(14)) });
   }
   
   public static String timeMillisToHumanString3(long t) {
     Calendar cal = Calendar.getInstance();
     cal.setTimeInMillis(t);
     return String.format("%04d%02d%02d%02d%02d%02d", new Object[] {
           Integer.valueOf(cal.get(1)), 
           Integer.valueOf(cal.get(2) + 1), 
           Integer.valueOf(cal.get(5)), 
           Integer.valueOf(cal.get(11)), 
           Integer.valueOf(cal.get(12)), 
           Integer.valueOf(cal.get(13)) });
   }
   
   public static double getDiskPartitionSpaceUsedPercent(String path) {
     if (null == path || path.isEmpty()) {
       return -1.0D;
     }
     try {
       File file = new File(path);
       
       if (!file.exists()) {
         return -1.0D;
       }
       long totalSpace = file.getTotalSpace();
       
       if (totalSpace > 0L) {
         long freeSpace = file.getFreeSpace();
         long usedSpace = totalSpace - freeSpace;
         
         return usedSpace / totalSpace;
       } 
     } catch (Exception e) {
       return -1.0D;
     } 
     
     return -1.0D;
   }
   
   public static int crc32(byte[] array) {
     if (array != null) {
       return crc32(array, 0, array.length);
     }
     
     return 0;
   }
   
   public static int crc32(byte[] array, int offset, int length) {
     CRC32 crc32 = new CRC32();
     crc32.update(array, offset, length);
     return (int)(crc32.getValue() & 0x7FFFFFFFL);
   }
   
   public static String bytes2string(byte[] src) {
     char[] hexChars = new char[src.length * 2];
     for (int j = 0; j < src.length; j++) {
       int v = src[j] & 0xFF;
       hexChars[j * 2] = HEX_ARRAY[v >>> 4];
       hexChars[j * 2 + 1] = HEX_ARRAY[v & 0xF];
     } 
     return new String(hexChars);
   }
   
   public static byte[] string2bytes(String hexString) {
     if (hexString == null || hexString.equals("")) {
       return null;
     }
     hexString = hexString.toUpperCase();
     int length = hexString.length() / 2;
     char[] hexChars = hexString.toCharArray();
     byte[] d = new byte[length];
     for (int i = 0; i < length; i++) {
       int pos = i * 2;
       d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
     } 
     return d;
   }
   
   private static byte charToByte(char c) {
     return (byte)"0123456789ABCDEF".indexOf(c);
   }
   
   public static byte[] uncompress(byte[] src) throws Exception {
     byte[] result = src;
     byte[] uncompressData = new byte[src.length];
     ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(src);
     InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(src.length);
     try {
       while (true) {
         int len = inflaterInputStream.read(uncompressData, 0, uncompressData.length);
         if (len <= 0) {
           break;
         }
         byteArrayOutputStream.write(uncompressData, 0, len);
       } 
       byteArrayOutputStream.flush();
       result = byteArrayOutputStream.toByteArray();
     } catch (Exception e) {
       throw e;
     } finally {
       byteArrayInputStream.close();
       inflaterInputStream.close();
       byteArrayOutputStream.close();
     } 
     return result;
   }
   
   public static byte[] compress(byte[] src, int level) throws IOException {
     byte[] result = src;
     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(src.length);
     Deflater defeater = new Deflater(level);
     DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, defeater);
     try {
       deflaterOutputStream.write(src);
       deflaterOutputStream.finish();
       deflaterOutputStream.close();
       result = byteArrayOutputStream.toByteArray();
     } catch (IOException e) {
       defeater.end();
       throw e;
     } finally {
       try {
         byteArrayOutputStream.close();
       } catch (IOException iOException) {}
 
       
       defeater.end();
     } 
     
     return result;
   }
   
   public static int asInt(String str, int defaultValue) {
     try {
       return Integer.parseInt(str);
     } catch (Exception e) {
       return defaultValue;
     } 
   }
   
   public static long asLong(String str, long defaultValue) {
     try {
       return Long.parseLong(str);
     } catch (Exception e) {
       return defaultValue;
     } 
   }
   
   public static String formatDate(Date date, String pattern) {
     SimpleDateFormat df = new SimpleDateFormat(pattern);
     return df.format(date);
   }
   
   public static Date parseDate(String date, String pattern) {
     SimpleDateFormat df = new SimpleDateFormat(pattern);
     try {
       return df.parse(date);
     } catch (ParseException e) {
       return null;
     } 
   }
   
   public static String responseCode2String(int code) {
     return Integer.toString(code);
   }
   
   public static String frontStringAtLeast(String str, int size) {
     if (str != null && 
       str.length() > size) {
       return str.substring(0, size);
     }
 
     
     return str;
   }
   
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
   
   public static boolean isInternalIP(byte[] ip) {
     if (ip.length != 4) {
       throw new RuntimeException("illegal ipv4 bytes");
     }
 
 
 
     
     if (ip[0] == 10)
     {
       return true; } 
     if (ip[0] == -84) {
       if (ip[1] >= 16 && ip[1] <= 31) {
         return true;
       }
     } else if (ip[0] == -64 && 
       ip[1] == -88) {
       return true;
     } 
     
     return false;
   }
   
   private static boolean ipCheck(byte[] ip) {
     if (ip.length != 4) {
       throw new RuntimeException("illegal ipv4 bytes");
     }
 
 
 
     
     if (ip[0] >= 1 && ip[0] <= 126) {
       if (ip[1] == 1 && ip[2] == 1 && ip[3] == 1) {
         return false;
       }
       if (ip[1] == 0 && ip[2] == 0 && ip[3] == 0) {
         return false;
       }
       return true;
     }  if (ip[0] >= Byte.MIN_VALUE && ip[0] <= -65) {
       if (ip[2] == 1 && ip[3] == 1) {
         return false;
       }
       if (ip[2] == 0 && ip[3] == 0) {
         return false;
       }
       return true;
     }  if (ip[0] >= -64 && ip[0] <= -33) {
       if (ip[3] == 1) {
         return false;
       }
       if (ip[3] == 0) {
         return false;
       }
       return true;
     } 
     return false;
   }
   
   public static String ipToIPv4Str(byte[] ip) {
     if (ip.length != 4) {
       return null;
     }
     return (ip[0] & 0xFF) + "." + (ip[1] & 0xFF) + "." + (
       ip[2] & 0xFF) + "." + (
       ip[3] & 0xFF);
   }
   
   public static byte[] getIP() {
     try {
       Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
       InetAddress ip = null;
       byte[] internalIP = null;
       while (allNetInterfaces.hasMoreElements()) {
         NetworkInterface netInterface = allNetInterfaces.nextElement();
         Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
         while (addresses.hasMoreElements()) {
           ip = addresses.nextElement();
           if (ip != null && ip instanceof java.net.Inet4Address) {
             byte[] ipByte = ip.getAddress();
             if (ipByte.length == 4 && 
               ipCheck(ipByte)) {
               if (!isInternalIP(ipByte))
                 return ipByte; 
               if (internalIP == null) {
                 internalIP = ipByte;
               }
             } 
           } 
         } 
       } 
       
       if (internalIP != null) {
         return internalIP;
       }
       throw new RuntimeException("Can not get local ip");
     }
     catch (Exception e) {
       throw new RuntimeException("Can not get local ip", e);
     } 
   }
   
   public static void deleteFile(File file) {
     if (!file.exists()) {
       return;
     }
     if (file.isFile()) {
       file.delete();
     } else if (file.isDirectory()) {
       File[] files = file.listFiles();
       for (File file1 : files) {
         deleteFile(file1);
       }
       file.delete();
     } 
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\UtilAll.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */