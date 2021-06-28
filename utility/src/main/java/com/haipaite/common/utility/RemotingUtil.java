 package com.haipaite.common.utility;

 import io.netty.channel.Channel;
 import io.netty.channel.ChannelFuture;
 import io.netty.channel.ChannelFutureListener;
 import io.netty.util.concurrent.Future;
 import io.netty.util.concurrent.GenericFutureListener;
 import java.io.IOException;
 import java.lang.reflect.Method;
 import java.net.InetAddress;
 import java.net.InetSocketAddress;
 import java.net.NetworkInterface;
 import java.net.SocketAddress;
 import java.nio.channels.Selector;
 import java.nio.channels.SocketChannel;
 import java.nio.channels.spi.SelectorProvider;
 import java.util.ArrayList;
 import java.util.Enumeration;












 public class RemotingUtil
 {
   public static final String OS_NAME = System.getProperty("os.name");

   private static boolean isLinuxPlatform = false;
   private static boolean isWindowsPlatform = false;

   static {
     if (OS_NAME != null && OS_NAME.toLowerCase().contains("linux")) {
       isLinuxPlatform = true;
     }

     if (OS_NAME != null && OS_NAME.toLowerCase().contains("windows")) {
       isWindowsPlatform = true;
     }
   }

   public static boolean isWindowsPlatform() {
     return isWindowsPlatform;
   }

   public static Selector openSelector() throws Exception {
     Selector result = null;
     if (isLinuxPlatform()) {
       Class<?> providerClazz = Class.forName("sun.nio.ch.EPollSelectorProvider");
       if (providerClazz != null) {
         Method method = providerClazz.getMethod("provider", new Class[0]);
         if (method != null) {
           SelectorProvider selectorProvider = (SelectorProvider)method.invoke(null, new Object[0]);
           if (selectorProvider != null) {
             result = selectorProvider.openSelector();
           }
         }
       }
     }

     if (result == null) {
       result = Selector.open();
     }

     return result;
   }

   public static boolean isLinuxPlatform() {
     return isLinuxPlatform;
   }


   public static String getLocalAddress() throws Exception {
     Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
     ArrayList<String> ipv4Result = new ArrayList<>();
     ArrayList<String> ipv6Result = new ArrayList<>();
     while (enumeration.hasMoreElements()) {
       NetworkInterface networkInterface = enumeration.nextElement();
       Enumeration<InetAddress> en = networkInterface.getInetAddresses();
       while (en.hasMoreElements()) {
         InetAddress address = en.nextElement();
         if (!address.isLoopbackAddress()) {
           if (address instanceof java.net.Inet6Address) {
             ipv6Result.add(normalizeHostAddress(address)); continue;
           }
           ipv4Result.add(normalizeHostAddress(address));
         }
       }
     }



     if (!ipv4Result.isEmpty()) {
       for (String ip : ipv4Result) {
         if (ip.startsWith("127.0") || ip.startsWith("192.168")) {
           continue;
         }

         return ip;
       }

       return ipv4Result.get(ipv4Result.size() - 1);
     }  if (!ipv6Result.isEmpty()) {
       return ipv6Result.get(0);
     }

     InetAddress localHost = InetAddress.getLocalHost();
     return normalizeHostAddress(localHost);
   }


   public static String normalizeHostAddress(InetAddress localHost) {
     if (localHost instanceof java.net.Inet6Address) {
       return "[" + localHost.getHostAddress() + "]";
     }
     return localHost.getHostAddress();
   }


   public static SocketAddress string2SocketAddress(String addr) {
     String[] s = addr.split(":");
     InetSocketAddress isa = new InetSocketAddress(s[0], Integer.parseInt(s[1]));
     return isa;
   }

   public static String socketAddress2String(SocketAddress addr) {
     StringBuilder sb = new StringBuilder();
     InetSocketAddress inetSocketAddress = (InetSocketAddress)addr;
     sb.append(inetSocketAddress.getAddress().getHostAddress());
     sb.append(":");
     sb.append(inetSocketAddress.getPort());
     return sb.toString();
   }

   public static SocketChannel connect(SocketAddress remote) {
     return connect(remote, 5000);
   }

   public static SocketChannel connect(SocketAddress remote, int timeoutMillis) {
     SocketChannel sc = null;
     try {
       sc = SocketChannel.open();
       sc.configureBlocking(true);
       sc.socket().setSoLinger(false, -1);
       sc.socket().setTcpNoDelay(true);
       sc.socket().setReceiveBufferSize(65536);
       sc.socket().setSendBufferSize(65536);
       sc.socket().connect(remote, timeoutMillis);
       sc.configureBlocking(false);
       return sc;
     } catch (Exception e) {
       if (sc != null) {
         try {
           sc.close();
         } catch (IOException e1) {
           e1.printStackTrace();
         }
       }


       return null;
     }
   }
   public static void closeChannel(Channel channel) {
     String addrRemote = RemotingHelper.parseChannelRemoteAddr(channel);
     channel.close().addListener((GenericFutureListener)new ChannelFutureListener() {
           public void operationComplete(ChannelFuture future) throws Exception {}
         });
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-utility-1.0.1.jar!\com\haipaite\commo\\utility\RemotingUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */