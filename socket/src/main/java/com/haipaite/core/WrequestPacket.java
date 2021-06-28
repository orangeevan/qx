 package com.haipaite.core;
 
 
 
 
 
 
 
 
 public class WrequestPacket
 {
   private short packetId;
   private byte[] data;
   
   public static WrequestPacket valueOf(short packetId, byte[] data) {
     WrequestPacket wp = new WrequestPacket();
     wp.setPacketId(packetId);
     wp.data = data;
     return wp;
   }
   
   public byte[] getData() {
     return this.data;
   }
   
   public void setData(byte[] data) {
     this.data = data;
   }
   
   public short getPacketId() {
     return this.packetId;
   }
   
   public void setPacketId(short packetId) {
     this.packetId = packetId;
   }
 
 
   
   public String toString() {
     return "PacketId: " + this.packetId + "  data: " + this.data;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\core\WrequestPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */