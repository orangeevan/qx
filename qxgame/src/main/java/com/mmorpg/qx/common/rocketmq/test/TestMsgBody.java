package com.mmorpg.qx.common.rocketmq.test;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.rocketmq.model.IMsgBody;

/**
 * @author wang ke
 * @description:
 * @since 20:42 2021/6/10
 */
public class TestMsgBody implements IMsgBody {
    @Protobuf
    private int t1;

    @Protobuf
    private String t2;

    public int getT1() {
        return t1;
    }

    public void setT1(int t1) {
        this.t1 = t1;
    }

    public String getT2() {
        return t2;
    }

    public void setT2(String t2) {
        this.t2 = t2;
    }
}
