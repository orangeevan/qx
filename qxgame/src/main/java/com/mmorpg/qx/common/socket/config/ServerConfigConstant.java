package com.mmorpg.qx.common.socket.config;

/**
 * @author wangke
 * @since v1.0 2016-1-11
 */
public interface ServerConfigConstant {

    /**
     * 分隔符定义
     */
    String SPLIT = ",";

    /**
     * 服务器的地址与端口配置，允许通过分隔符","指定多个地址
     */
    String KEY_ADDRESS = "server.socket.address";
    /**
     * 服务器接收最大包体长度
     */
    String PACKET_MAXLENGTH = "server.socket.maxlength";
    /**
     * 读取缓存大小设置
     */
    String KEY_BUFFER_READ = "server.socket.buffer.read";
    /**
     * 写入缓存大小设置
     */
    String KEY_BUFFER_WRITE = "server.socket.buffer.write";
    /**
     * 连接超时设置
     */
    String KEY_TIMEOUT = "server.socket.timeout";
    /**
     * 最小线程数设置
     */
    String KEY_POOL_MIN = "server.socket.pool.min";
    /**
     * 最大线程数设置
     */
    String KEY_POOL_MAX = "server.socket.pool.max";
    /**
     * 线程空闲时间设置，单位:millisecond
     */
    String KEY_POOL_IDLE = "server.socket.pool.idle";

    /**
     * 设置服务器是否自动启动
     */
    String KEY_AUTO_START = "server.config.auto_start";

    /**
     * RPC服务端口
     */
    String RPC_SERVER_PORT = "server.rpc.port";

    /**
     * Proto文件生成目录
     */
    String PROTO_IDL_DIR = "server.proto.dir";
    /**
     * 必须的配置键
     */
    String[] KEYS = {KEY_ADDRESS, KEY_BUFFER_READ, KEY_BUFFER_WRITE, KEY_TIMEOUT, KEY_POOL_MIN, KEY_POOL_MAX,
            KEY_POOL_IDLE};

    byte[] KEY = new byte[]{'h', 'a', 'i', 'p', 'a', 'i', 't', 'e', 'h', 'a', 'i', 'p', 'a', 'i', 't', 'e'};

    int MAGIC_STR_LEN = 10;
    String MAGIC_STR = "1234567890abcdefghijklmnopqrstuvwxyz=+%ABCDEFJHIJKLMNOPQRSTUWXYZ";
    /**
     * 心跳周期毫秒
     */
    int PLAYER_HEAR_BEAT_PERIOD = 60 * 1000;
}
