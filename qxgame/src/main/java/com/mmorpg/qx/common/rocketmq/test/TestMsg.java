package com.mmorpg.qx.common.rocketmq.test;

import com.mmorpg.qx.common.rocketmq.model.IMsg;

/**
 * @author wang ke
 * @description:
 * @since 20:44 2021/6/10
 */
public class TestMsg implements IMsg {
    @Override
    public String getTopic() {
        return  topic;
    }

    @Override
    public String getSubTag() {
        return subTag;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public Object arg() {
        return arg;
    }

    private String topic;

    private String subTag;

    private String group;

    private TestMsgBody msgBody;

    private Object arg;

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setSubTag(String subTag) {
        this.subTag = subTag;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setMsgBody(TestMsgBody msgBody) {
        this.msgBody = msgBody;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }

    @Override
    public TestMsgBody getMsgBody() {
        return this.msgBody;
    }
}
