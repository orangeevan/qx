 package com.haipaite.common.utility;
 
 import java.net.InetAddress;
 import java.net.UnknownHostException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class GetLocalIp
 {
   public static String getLocalHostIP() {
     String ip;
     try {
       InetAddress addr = InetAddress.getLocalHost();
       
       ip = addr.getHostAddress();
     } catch (Exception ex) {
       ip = "";
     } 
     
     return ip;
   }
 
 
 
 
 
 
   
   public static String getLocalHostName() {
     String hostName;
     try {
       InetAddress addr = InetAddress.getLocalHost();
       
       hostName = addr.getHostName();
     } catch (Exception ex) {
       hostName = "";
     } 
     
     return hostName;
   }
 
 
 
 
 
 
   
   public static String[] getAllLocalHostIP() {
     String[] ret = null;
     
     try {
       String hostName = getLocalHostName();
       if (hostName.length() > 0) {
         
         InetAddress[] addrs = InetAddress.getAllByName(hostName);
         if (addrs.length > 0) {
           ret = new String[addrs.length];
           for (int i = 0; i < addrs.length; i++)
           {
             ret[i] = addrs[i].getHostAddress();
           }
         }
       
       } 
     } catch (Exception ex) {
       ret = null;
     } 
     
     return ret;
   }
   
   public static int ip2Int(String ip) throws UnknownHostException {
     InetAddress address = InetAddress.getByName(ip);
     byte[] bytes = address.getAddress();
     
     int a = byte2int(bytes[0]);
     int b = byte2int(bytes[1]);
     int c = byte2int(bytes[2]);
     int d = byte2int(bytes[3]);
     int result = a << 24 | b << 16 | c << 8 | d;
     return result;
   }
   
   public static int byte2int(byte b) {
     int l = b & Byte.MAX_VALUE;
     if (b < 0) {
       l |= 0x80;
     }
     return l;
   }
   
   public static void main(String[] args) throws UnknownHostException {
     System.out.println("本机IP：" + getLocalHostIP());
     System.out.println("本机IPtoInt：" + ip2Int(getLocalHostIP()));
     System.out.println("本地主机名字为：" + getLocalHostName());
     
     String[] localIP = getAllLocalHostIP();
     for (int i = 0; i < localIP.length; i++) {
       System.out.println(localIP[i]);
     }
     
     InetAddress baidu = InetAddress.getByName("www.baidu.com");
     System.out.println("baidu : " + baidu);
     System.out.println("baidu IP: " + baidu.getHostAddress());
     System.out.println("baidu HostName: " + baidu.getHostName());
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\GetLocalIp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */