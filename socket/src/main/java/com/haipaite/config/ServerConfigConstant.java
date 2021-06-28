 package com.haipaite.config;

























 public interface ServerConfigConstant
 {
   public static final String SPLIT = ",";
   public static final String KEY_ADDRESS = "server.socket.address";
   public static final String PACKET_MAXLENGTH = "server.socket.maxlength";
   public static final String KEY_BUFFER_READ = "server.socket.buffer.read";
   public static final String KEY_BUFFER_WRITE = "server.socket.buffer.write";
   public static final String KEY_TIMEOUT = "server.socket.timeout";
   public static final String KEY_POOL_MIN = "server.socket.pool.min";
   public static final String KEY_POOL_MAX = "server.socket.pool.max";
   public static final String KEY_POOL_IDLE = "server.socket.pool.idle";
   public static final String KEY_AUTO_START = "server.config.auto_start";
   public static final String RPC_SERVER_PORT = "server.rpc.port";
   public static final String PROTO_IDL_DIR = "server.proto.dir";
   public static final String[] KEYS = new String[] { "server.socket.address", "server.socket.buffer.read", "server.socket.buffer.write", "server.socket.timeout", "server.socket.pool.min", "server.socket.pool.max", "server.socket.pool.idle" };


   public static final byte[] KEY = new byte[] { 104, 97, 105, 112, 97, 105, 116, 101, 104, 97, 105, 112, 97, 105, 116, 101 };
   public static final int MAGIC_STR_LEN = 10;
   public static final String MAGIC_STR = "1234567890abcdefghijklmnopqrstuvwxyz=+%ABCDEFJHIJKLMNOPQRSTUWXYZ";
 }


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-socket-1.0.1.jar!\com\haipaite\config\ServerConfigConstant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */