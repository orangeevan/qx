 package com.haipaite.core;
 
 
 
 
 
 
 public class WresponsePacket
 {
   private short packetId;
   private byte[] data;
   
   public static WresponsePacket valueOf(short packetId, byte[] data) {
     WresponsePacket wp = new WresponsePacket();
     wp.setPacketId(packetId);
     wp.data = data;
     return wp;
   }
   
   public short getPacketId() {
     return this.packetId;
   }
   
   public void setPacketId(short packetId) {
     this.packetId = packetId;
   }
   
   public byte[] getData() {
     return this.data;
   }
   
   public void setData(byte[] data) {
     this.data = data;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\core\WresponsePacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */