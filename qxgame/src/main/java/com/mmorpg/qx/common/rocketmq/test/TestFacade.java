package com.mmorpg.qx.common.rocketmq.test;

import com.haipaite.common.utility.RandomUtils;
import com.mmorpg.qx.common.identify.manager.ServerConfigValue;
import com.mmorpg.qx.common.rocketmq.anno.MQSocketClass;
import com.mmorpg.qx.common.rocketmq.anno.MQSocketMethod;
import com.mmorpg.qx.common.rocketmq.manager.ProducerManager;
import com.mmorpg.qx.common.rocketmq.service.IProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wang ke
 * @description:
 * @since 20:59 2021/6/10
 */
@Component
@MQSocketClass
public class TestFacade {

    @Autowired
    private IProducerService producerService;


    @MQSocketMethod
    public void test(TestMsgBody msg) {
        System.err.println(msg);
        TestMsg testMsg = new TestMsg();
        TestMsgBody testMsgBody = new TestMsgBody();
        testMsgBody.setT1(1 + RandomUtils.nextInt());
        testMsgBody.setT2("test" + RandomUtils.nextInt());
        testMsg.setMsgBody(testMsgBody);
        testMsg.setGroup("test");
        testMsg.setTopic("testTopic");
        testMsg.setSubTag("TestMsgResponse");
        testMsg.setArg(ServerConfigValue.getInstance().getRocketMqConsumerGroup());
        boolean result = producerService.sendMsg(ProducerManager.getMQProducer(), testMsg);
        if (result) {
            System.err.println("逻辑服收到请求后，返回消息");
        }
    }

    @MQSocketMethod
    private void testResponse(TestMsgResponse msgResponse) {
        //收到逻辑服消息，处理完后客户端下发
        System.err.println("收到逻辑服消息，处理完后客户端下发");
        System.err.println(msgResponse);
    }

}
