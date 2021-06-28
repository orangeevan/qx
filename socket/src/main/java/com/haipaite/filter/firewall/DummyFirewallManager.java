 package com.haipaite.filter.firewall;
 
 import com.haipaite.core.WrequestPacket;
 import com.haipaite.core.Wsession;
 
 public class DummyFirewallManager
   implements FirewallManager
 {
   public boolean packetFilter(Wsession session, WrequestPacket packet) {
     return true;
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\filter\firewall\DummyFirewallManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */