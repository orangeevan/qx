 package com.haipaite.core;
 
 import com.haipaite.dispatcher.SocketPacketHandler;
 import com.haipaite.filter.firewall.FirewallRecord;
 import io.netty.channel.Channel;
 import io.netty.channel.ChannelFuture;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.atomic.AtomicBoolean;
 import java.util.concurrent.atomic.AtomicInteger;
 
 
 
 
 
 
 
 
 
 
 public class Wsession
 {
   private static final AtomicInteger SEQ = new AtomicInteger(1);
 
   
   private int id;
   
   private Channel channel;
   
   private int dispatcherHashCode;
   
   private FirewallRecord firewallRecord = new FirewallRecord();
   
   private Map<String, Object> attributes = new ConcurrentHashMap<>();
   
   public static Wsession valueOf(Channel channel) {
     Wsession session = new Wsession();
     session.channel = channel;
     session.id = SEQ.incrementAndGet();
     return session;
   }
 
   
   private AtomicBoolean flushTimer;
   
   public Channel getChannel() {
     return this.channel;
   }
   
   public void setChannel(Channel channel) {
     this.channel = channel;
   }
   
   public Map<String, Object> getAttributes() {
     return this.attributes;
   }
   
   public void setAttributes(Map<String, Object> attributes) {
     this.attributes = attributes;
   }
   
   public FirewallRecord getFirewallRecord() {
     return this.firewallRecord;
   }
   
   public void setFirewallRecord(FirewallRecord firewallRecord) {
     this.firewallRecord = firewallRecord;
   }
   
   public int getId() {
     return this.id;
   }
   
   public void setId(int id) {
     this.id = id;
   }
 
 
 
 
 
   
   public int selectDispatcherHashCode() {
     if (this.dispatcherHashCode <= 0) {
       return Math.abs(this.channel.hashCode());
     }
     return this.dispatcherHashCode;
   }
   
   public int getDispatcherHashCode() {
     return selectDispatcherHashCode();
   }
   
   public void setDispatcherHashCode(int dispatcherHashCode) {
     this.dispatcherHashCode = Math.abs(dispatcherHashCode);
   }
   
   public ChannelFuture sendPacket(Object packet) {
     return sendPacket(packet, false);
   }
   
   public ChannelFuture sendPacket(Object packet, boolean flushNow) {
     ChannelFuture future = SocketPacketHandler.getInstance().sendMsg(this, this.channel, packet, flushNow);
     return future;
   }
   private Wsession() {
     this.flushTimer = new AtomicBoolean(false);
   }
   public AtomicBoolean getFlushTimer() {
     return this.flushTimer;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\core\Wsession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */