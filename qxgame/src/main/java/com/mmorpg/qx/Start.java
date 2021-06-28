package com.mmorpg.qx;

import com.haipaite.common.threadpool.IdentityEventExecutorGroup;
import com.haipaite.common.utility.DateUtils;
import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.FirewallManagerImpl;
import com.mmorpg.qx.common.socket.firewall.FirewallManager;
import com.mmorpg.qx.common.socket.server.Wserver;
import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * 启动类
 *
 * @author wang ke
 * @since v1.0 2018年2月24日
 */
public class Start {

    private static final Logger logger = SysLoggerFactory.getLogger(Start.class);

    /**
     * 默认的上下文配置名
     */
    private static final String DEFAULT_APPLICATION_CONTEXT = "applicationContext.xml";

    public static void main(String[] args) {
        Debug.debug = false;
        ClassPathXmlApplicationContext applicationContext = null;
        long start = System.nanoTime();
        try {
            IdentityEventExecutorGroup.init(24);
            applicationContext = new ClassPathXmlApplicationContext(DEFAULT_APPLICATION_CONTEXT);
            // 生成地图和怪物
            //SpawnManager.getInstance().spawnAll();
            // 最后启动通讯组件
            Wserver wserver = applicationContext.getBean(Wserver.class);
            FirewallManager firewallManager = (FirewallManager) new FirewallManagerImpl();
            wserver.bind(firewallManager);
            System.gc();
        } catch (Throwable e) {
            String message = MessageFormatter.format("", e.getMessage()).getMessage();
            logger.error(message, e);
            e.printStackTrace();
            Runtime.getRuntime().exit(-1);
        }
        applicationContext.registerShutdownHook();
        applicationContext.start();
        System.gc();
        String message1 = MessageFormatter.format("服务器已经启动 - [{}]", DateUtils.date2String(new Date(), DateUtils.PATTERN_DATE_TIME)).getMessage();
        logger.error(message1);
        String message2 = MessageFormatter.format("服务器当前时区 - [{}]", TimeZone.getDefault()).getMessage();
        logger.error(message2);
        String message3 = MessageFormatter.format("服务器MD5 - [{}]", JsonUtils.object2String(args)).getMessage();
        logger.error(message3);
        long end = System.nanoTime() - start;
        logger.error("消耗了 " + TimeUnit.NANOSECONDS.toMillis(end) + " ms (╯﹏╰)");
        while (applicationContext.isActive()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("服务器主线程被非法打断", e);
                }
            }
        }
        while (applicationContext.isRunning()) {
            Thread.yield();
        }
        String message = MessageFormatter.format("服务器已经关闭 - [{}]", DateUtils.date2String(new Date(), DateUtils.PATTERN_DATE_TIME)).getMessage();
        logger.error(message);
        System.err.println(message);
    }


}
