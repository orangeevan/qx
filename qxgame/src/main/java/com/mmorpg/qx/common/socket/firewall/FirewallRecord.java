package com.mmorpg.qx.common.socket.firewall;

/**
 * 流量控制防火墙记录
 *
 * @author wangke
 * @since v1.0 2016-1-14
 */
public class FirewallRecord {

    /**
     * 最大违规次数
     */
    private static int MAX_VIOLATE_TIMES = 5;
    /**
     * 每秒收到的字节数限制
     */
    private static int BYTES_IN_SECOND_LIMIT = 40960;
    /**
     * 每秒收到的数据包次数限制
     */
    private static int TIMES_IN_SECOND_LIMIT = 128;

    /**
     * 设置最大违规次数
     *
     * @param times
     */
    public static void setMaxViolateTimes(int times) {
        MAX_VIOLATE_TIMES = times;
    }

    /**
     * 设置每秒收到的字节数限制
     *
     * @param size
     */
    public static void setBytesInSecondLimit(int size) {
        BYTES_IN_SECOND_LIMIT = size;
    }

    /**
     * 设置每秒收到的数据包次数限制
     *
     * @param size
     */
    public static void setTimesInSecondLimit(int size) {
        TIMES_IN_SECOND_LIMIT = size;
    }

    /**
     * 最后记录时间
     */
    private long lastSecond = 0;
    /**
     * 当前秒收到的字节数
     */
    private int bytesInSecond = 0;
    /**
     * 当前秒收到的数据包次数
     */
    private int timesInSecond = 0;

    /**
     * 违规次数
     */
    private int violateTime = 0;

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("{bytesInSecond:" + bytesInSecond + ",timesInSecond:" + timesInSecond + ",violateTime:"
                + violateTime + "}");
        return str.toString();
    }

    /**
     * 检查是否违规
     *
     * @param bytes 当次收到的数据包大小
     * @return true:本次违规；false:没有违规
     */
    public boolean check(int bytes) {
        // 当前毫秒
        long ms = System.currentTimeMillis();
        // 当前秒数
        long currentSecond = ms / 1000;

        if (currentSecond != lastSecond) {
            // 重置状态
            lastSecond = currentSecond;
            bytesInSecond = 0;
            timesInSecond = 0;
        }

        // 累计数据
        bytesInSecond += bytes;
        timesInSecond++;

        // 边界判断
        if (timesInSecond >= TIMES_IN_SECOND_LIMIT || bytesInSecond >= BYTES_IN_SECOND_LIMIT) {
            violateTime++;
            return true;
        }

        //System.err.println("流量包字节大小： "+bytesInSecond);
        return false;
    }

    /**
     * 检查是否需要被阻止
     *
     * @return true:要阻止；false:不需要阻止
     */
    public boolean isBlock() {
        return violateTime >= MAX_VIOLATE_TIMES;
    }

    // Getter and Setter ...

    public long getLastSecond() {
        return lastSecond;
    }

    public int getBytesInSecond() {
        return bytesInSecond;
    }

    public int getTimesInSecond() {
        return timesInSecond;
    }

    public int getViolateTime() {
        return violateTime;
    }

}
