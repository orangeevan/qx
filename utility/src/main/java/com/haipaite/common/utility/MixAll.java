 package com.haipaite.common.utility;
 
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.management.ManagementFactory;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.net.InetAddress;
 import java.net.NetworkInterface;
 import java.net.SocketException;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.concurrent.atomic.AtomicLong;
 
 
 
 
 
 
 
 
 
 public class MixAll
 {
   public static final String DEFAULT_NAMESRV_ADDR_LOOKUP = "jmenv.tbsite.net";
   public static final String CID_RMQ_SYS_PREFIX = "CID_RMQ_SYS_";
   public static final String DEFAULT_CHARSET = "UTF-8";
   public static final String RETRY_GROUP_TOPIC_PREFIX = "%RETRY%";
   public static final String DLQ_GROUP_TOPIC_PREFIX = "%DLQ%";
   public static final String SYSTEM_TOPIC_PREFIX = "rmq_sys_";
   
   public static String getWSAddr() {
     String wsDomainName = System.getProperty("rocketmq.namesrv.domain", "jmenv.tbsite.net");
     String wsDomainSubgroup = System.getProperty("rocketmq.namesrv.domain.subgroup", "nsaddr");
     String wsAddr = "http://" + wsDomainName + ":8080/rocketmq/" + wsDomainSubgroup;
     if (wsDomainName.indexOf(":") > 0) {
       wsAddr = "http://" + wsDomainName + "/rocketmq/" + wsDomainSubgroup;
     }
     return wsAddr;
   }
   
   public static String getRetryTopic(String consumerGroup) {
     return "%RETRY%" + consumerGroup;
   }
   
   public static boolean isSysConsumerGroup(String consumerGroup) {
     return consumerGroup.startsWith("CID_RMQ_SYS_");
   }
   
   public static boolean isSystemTopic(String topic) {
     return topic.startsWith("rmq_sys_");
   }
   
   public static String getDLQTopic(String consumerGroup) {
     return "%DLQ%" + consumerGroup;
   }
   
   public static String brokerVIPChannel(boolean isChange, String brokerAddr) {
     if (isChange) {
       String[] ipAndPort = brokerAddr.split(":");
       String brokerAddrNew = ipAndPort[0] + ":" + (Integer.parseInt(ipAndPort[1]) - 2);
       return brokerAddrNew;
     } 
     return brokerAddr;
   }
 
   
   public static long getPID() {
     String processName = ManagementFactory.getRuntimeMXBean().getName();
     if (processName != null && processName.length() > 0) {
       try {
         return Long.parseLong(processName.split("@")[0]);
       } catch (Exception e) {
         return 0L;
       } 
     }
     
     return 0L;
   }
 
   
   public static void string2File(String str, String fileName) throws IOException {
     String tmpFile = fileName + ".tmp";
     string2FileNotSafe(str, tmpFile);
     
     String bakFile = fileName + ".bak";
     String prevContent = file2String(fileName);
     if (prevContent != null) {
       string2FileNotSafe(prevContent, bakFile);
     }
     
     File file = new File(fileName);
     file.delete();
     
     file = new File(tmpFile);
     file.renameTo(new File(fileName));
   }
   
   public static void string2FileNotSafe(String str, String fileName) throws IOException {
     File file = new File(fileName);
     File fileParent = file.getParentFile();
     if (fileParent != null) {
       fileParent.mkdirs();
     }
     FileWriter fileWriter = null;
     
     try {
       fileWriter = new FileWriter(file);
       fileWriter.write(str);
     } catch (IOException e) {
       throw e;
     } finally {
       if (fileWriter != null) {
         fileWriter.close();
       }
     } 
   }
   
   public static String file2String(String fileName) throws IOException {
     File file = new File(fileName);
     return file2String(file);
   }
   
   public static String file2String(File file) throws IOException {
     if (file.exists()) {
       boolean result; byte[] data = new byte[(int)file.length()];
 
       
       FileInputStream inputStream = null;
       try {
         inputStream = new FileInputStream(file);
         int len = inputStream.read(data);
         result = (len == data.length);
       } finally {
         if (inputStream != null) {
           inputStream.close();
         }
       } 
       
       if (result) {
         return new String(data);
       }
     } 
     return null;
   }
   
   public static String file2String(URL url) {
     InputStream in = null;
     
     try { URLConnection urlConnection = url.openConnection();
       urlConnection.setUseCaches(false);
       in = urlConnection.getInputStream();
       int len = in.available();
       byte[] data = new byte[len];
       in.read(data, 0, len);
       return new String(data, "UTF-8"); }
     catch (Exception exception) {  }
     finally
     { if (null != in) {
         try {
           in.close();
         } catch (IOException iOException) {}
       } }
 
 
     
     return null;
   }
 
   
   public static String properties2String(Properties properties) {
     StringBuilder sb = new StringBuilder();
     for (Map.Entry<Object, Object> entry : properties.entrySet()) {
       if (entry.getValue() != null) {
         sb.append(entry.getKey().toString() + "=" + entry.getValue().toString() + "\n");
       }
     } 
     return sb.toString();
   }
   
   public static Properties string2Properties(String str) throws Exception {
     Properties properties = new Properties();
     InputStream in = new ByteArrayInputStream(str.getBytes("UTF-8"));
     properties.load(in);
     return properties;
   }
   
   public static Properties object2Properties(Object object) throws Exception {
     Properties properties = new Properties();
     Field[] fields = object.getClass().getDeclaredFields();
     for (Field field : fields) {
       if (!Modifier.isStatic(field.getModifiers())) {
         String name = field.getName();
         if (!name.startsWith("this")) {
           Object value = null;
           field.setAccessible(true);
           value = field.get(object);
           if (value != null) {
             properties.setProperty(name, value.toString());
           }
         } 
       } 
     } 
     return properties;
   }
   
   public static void properties2Object(Properties p, Object object) throws Throwable {
     Method[] methods = object.getClass().getMethods();
     for (Method method : methods) {
       String mn = method.getName();
       if (mn.startsWith("set")) {
         String tmp = mn.substring(4);
         String first = mn.substring(3, 4);
         String key = first.toLowerCase() + tmp;
         String property = p.getProperty(key);
         if (property != null) {
           Class<?>[] pt = method.getParameterTypes();
           if (pt != null && pt.length > 0) {
             String cn = pt[0].getSimpleName();
             Object arg = null;
             if (cn.equals("int") || cn.equals("Integer")) {
               arg = Integer.valueOf(Integer.parseInt(property));
             } else if (cn.equals("long") || cn.equals("Long")) {
               arg = Long.valueOf(Long.parseLong(property));
             } else if (cn.equals("double") || cn.equals("Double")) {
               arg = Double.valueOf(Double.parseDouble(property));
             } else if (cn.equals("boolean") || cn.equals("Boolean")) {
               arg = Boolean.valueOf(Boolean.parseBoolean(property));
             } else if (cn.equals("float") || cn.equals("Float")) {
               arg = Float.valueOf(Float.parseFloat(property));
             } else if (cn.equals("String")) {
               arg = property;
             } else {
               continue;
             } 
             method.invoke(object, new Object[] { arg });
           } 
         } 
       } 
       continue;
     } 
   }
   public static boolean isPropertiesEqual(Properties p1, Properties p2) {
     return p1.equals(p2);
   }
   
   public static List<String> getLocalInetAddress() {
     List<String> inetAddressList = new ArrayList<>();
     try {
       Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
       while (enumeration.hasMoreElements()) {
         NetworkInterface networkInterface = enumeration.nextElement();
         Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
         while (addrs.hasMoreElements()) {
           inetAddressList.add(((InetAddress)addrs.nextElement()).getHostAddress());
         }
       } 
     } catch (SocketException e) {
       throw new RuntimeException("get local inet address fail", e);
     } 
     
     return inetAddressList;
   }
   
   private static String localhost() {
     try {
       return InetAddress.getLocalHost().getHostAddress();
     } catch (Throwable e) {
       try {
         String candidatesHost = getLocalhostByNetworkInterface();
         if (candidatesHost != null) {
           return candidatesHost;
         }
       } catch (Exception exception) {}
 
       
       throw new RuntimeException("InetAddress java.net.InetAddress.getLocalHost() throws UnknownHostException" + e);
     } 
   }
 
   
   public static String getLocalhostByNetworkInterface() throws SocketException {
     List<String> candidatesHost = new ArrayList<>();
     Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
     
     while (enumeration.hasMoreElements()) {
       NetworkInterface networkInterface = enumeration.nextElement();
       
       if ("docker0".equals(networkInterface.getName()) || !networkInterface.isUp()) {
         continue;
       }
       Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
       while (addrs.hasMoreElements()) {
         InetAddress address = addrs.nextElement();
         if (address.isLoopbackAddress()) {
           continue;
         }
         
         if (address instanceof java.net.Inet6Address) {
           candidatesHost.add(address.getHostAddress());
           continue;
         } 
         return address.getHostAddress();
       } 
     } 
     
     if (!candidatesHost.isEmpty()) {
       return candidatesHost.get(0);
     }
     return null;
   }
   
   public static boolean compareAndIncreaseOnly(AtomicLong target, long value) {
     long prev = target.get();
     while (value > prev) {
       boolean updated = target.compareAndSet(prev, value);
       if (updated)
         return true; 
       prev = target.get();
     } 
     return false;
   }
   
   public static String humanReadableByteCount(long bytes, boolean si) {
     int unit = si ? 1000 : 1024;
     if (bytes < unit)
       return bytes + " B"; 
     int exp = (int)(Math.log(bytes) / Math.log(unit));
     String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
     return String.format("%.1f %sB", new Object[] { Double.valueOf(bytes / Math.pow(unit, exp)), pre });
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\MixAll.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */