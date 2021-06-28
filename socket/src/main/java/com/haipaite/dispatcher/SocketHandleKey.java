 package com.haipaite.dispatcher;
 
 
 
 
 
 
 
 public class SocketHandleKey
 {
   private short packetId;
   
   public static SocketHandleKey valueOf(short packetId) {
     SocketHandleKey key = new SocketHandleKey();
     key.packetId = packetId;
     return key;
   }
 
   
   public int hashCode() {
     int prime = 31;
     int result = 1;
     result = 31 * result + this.packetId;
     return result;
   }
 
   
   public boolean equals(Object obj) {
     if (this == obj) {
       return true;
     }
     if (obj == null) {
       return false;
     }
     if (getClass() != obj.getClass()) {
       return false;
     }
     SocketHandleKey other = (SocketHandleKey)obj;
     if (this.packetId != other.packetId) {
       return false;
     }
     return true;
   }
 
   
   public String toString() {
     return "{packetId:" + this.packetId + "}";
   }
   
   public short getPacketId() {
     return this.packetId;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\dispatcher\SocketHandleKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */