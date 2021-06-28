 package com.haipaite.common.utility;
 
 import io.netty.channel.Channel;
 import java.net.InetSocketAddress;
 import java.net.SocketAddress;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class RemotingHelper
 {
   public static final String ROCKETMQ_REMOTING = "RocketmqRemoting";
   public static final String DEFAULT_CHARSET = "UTF-8";
   
   public static String exceptionSimpleDesc(Throwable e) {
     StringBuffer sb = new StringBuffer();
     if (e != null) {
       sb.append(e.toString());
       
       StackTraceElement[] stackTrace = e.getStackTrace();
       if (stackTrace != null && stackTrace.length > 0) {
         StackTraceElement elment = stackTrace[0];
         sb.append(", ");
         sb.append(elment.toString());
       } 
     } 
     
     return sb.toString();
   }
   
   public static SocketAddress string2SocketAddress(String addr) {
     String[] s = addr.split(":");
     InetSocketAddress isa = new InetSocketAddress(s[0], Integer.parseInt(s[1]));
     return isa;
   }
 
 
   
   public static String parseChannelRemoteAddr(Channel channel) {
     if (null == channel) {
       return "";
     }
     SocketAddress remote = channel.remoteAddress();
     String addr = (remote != null) ? remote.toString() : "";
     
     if (addr.length() > 0) {
       int index = addr.lastIndexOf("/");
       if (index >= 0) {
         return addr.substring(index + 1);
       }
       
       return addr;
     } 
     
     return "";
   }
   
   public static String parseSocketAddressAddr(SocketAddress socketAddress) {
     if (socketAddress != null) {
       String addr = socketAddress.toString();
       
       if (addr.length() > 0) {
         return addr.substring(1);
       }
     } 
     return "";
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\RemotingHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */