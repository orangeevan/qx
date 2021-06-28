 package com.haipaite.core;

 import io.netty.channel.ChannelId;
 import java.util.concurrent.ConcurrentHashMap;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.stereotype.Component;









 @Component
 public class SessionManager
 {
   private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);


   private ConcurrentHashMap<ChannelId, Wsession> allSessions = new ConcurrentHashMap<>();

   public void add(Wsession session) {
     if (!this.allSessions.containsKey(session.getChannel().id())) {
       this.allSessions.put(session.getChannel().id(), session);
     } else {

       logger.error(String.format("channelI[%s],ip[%s]重复注册sessionManager", new Object[] { session.getChannel().id().asShortText(), session
               .getChannel().remoteAddress() }));
     }
   }

   public int ipSessionCount(String ip) {
     int count = 0;
     for (Wsession session : this.allSessions.values()) {
       if (session.getChannel().remoteAddress().toString().contains(ip)) {
         count++;
       }
     }
     return count;
   }

   public Wsession getSession(ChannelId channelId) {
     return this.allSessions.get(channelId);
   }

   public void remove(ChannelId id) {
     this.allSessions.remove(id);
   }
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\core\SessionManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */