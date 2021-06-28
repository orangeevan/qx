package com.mmorpg.qx.common.rocketmq.test;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.rocketmq.model.IMsgBody;

/**
 * @author wang ke
 * @description:
 * @since 10:55 2021/6/16
 */
public class TestMsgResponse implements IMsgBody {
    @Protobuf
    private String t1;

    public String getT1() {
        return t1;
    }

    public void setT1(String t1) {
        this.t1 = t1;
    }
}
