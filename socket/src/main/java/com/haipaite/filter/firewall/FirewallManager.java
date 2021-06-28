package com.haipaite.filter.firewall;

import com.haipaite.core.WrequestPacket;
import com.haipaite.core.Wsession;

public interface FirewallManager {
  boolean packetFilter(Wsession paramWsession, WrequestPacket paramWrequestPacket);
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\filter\firewall\FirewallManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */