package com.mmorpg.qx.common.socket.core;

import com.mmorpg.qx.common.socket.dispatcher.SocketPacketHandler;
import com.mmorpg.qx.common.socket.firewall.FirewallRecord;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 会话
 *
 * @author wangke
 * @since v1.0 2016年6月3日
 */
public class Wsession {

    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private int id;

    private Channel channel;

    private int dispatcherHashCode;

    /**
     * 流量记录
     */
    private FirewallRecord firewallRecord = new FirewallRecord();

    private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    public static Wsession valueOf(Channel channel) {
        Wsession session = new Wsession();
        session.channel = channel;
        SEQ.compareAndSet(Integer.MAX_VALUE, 1);
        session.id = SEQ.incrementAndGet();
        //设置分发线程hashcode
        session.setDispatcherHashCode(session.id);
        return session;
    }

    private Wsession() {
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public FirewallRecord getFirewallRecord() {
        return firewallRecord;
    }

    public void setFirewallRecord(FirewallRecord firewallRecord) {
        this.firewallRecord = firewallRecord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 当dispatcherHashCode没有初始化时选择channel的hashcode作为分发code
     *
     * @return int
     */
    public int selectDispatcherHashCode() {
//        if (dispatcherHashCode <= 0) {
//            dispatcherHashCode = Math.abs(channel.hashCode());
//        }
        return dispatcherHashCode;
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
        return SocketPacketHandler.getInstance().sendMsg(this, channel, packet, flushNow);
    }

    private AtomicBoolean flushTimer = new AtomicBoolean(false);

    public AtomicBoolean getFlushTimer() {
        return flushTimer;
    }

    public void copy(Wsession copy) {
        this.attributes = new ConcurrentHashMap<String, Object>(copy.attributes);
    }

}
