package com.mmorpg.qx.common.logger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wang ke
 * @description:系统日志
 * @since 15:30 2020-08-10
 */
public class SysLoggerFactory {

    private static final ConcurrentHashMap<String, Logger> container = new ConcurrentHashMap<>();

    private static final String LOG_PATH = "${LOG_HOME}/logs/";
    private static final String Pattern = "[%date{yyyy-MM-dd HH:mm:ss}] [%-5level][%thread] [%logger:%line] - %msg%n";

    public static Logger getLogger(Class c) {
        String name = c.getSimpleName();
        Logger logger = container.get(name);
        if(logger != null) {
            return logger;
        }
        synchronized (SysLoggerFactory.class) {
            logger = container.get(name);
            if(logger != null) {
                return logger;
            }
            logger = build(name);
            container.put(name,logger);
        }
        return logger;
    }

    private  static Logger build(String name) {
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.SIMPLIFIED_CHINESE);
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(name);
        logger.setAdditive(true);
        RollingFileAppender appender = new RollingFileAppender();
        appender.setContext(context);
        appender.setName("FILE-" + name);
        appender.setFile(OptionHelper.substVars(SysLoggerFactory.LOG_PATH +format.format(new Date())+ File.separator+ name + ".log",context));
        appender.setAppend(true);
        appender.setPrudent(false);
        SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();
        String fp = OptionHelper.substVars(SysLoggerFactory.LOG_PATH + format.format(new Date())+File.separator+ name + "/.%d{yyyy-MM-dd}.%i.log.zip",context);
        policy.setMaxFileSize(FileSize.valueOf("1024MB"));
        policy.setFileNamePattern(fp);
        policy.setMaxHistory(30);
        //policy.setTotalSizeCap(FileSize.valueOf("32GB"));
        policy.setParent(appender);
        policy.setContext(context);
        policy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(Pattern);
        encoder.start();

        appender.setRollingPolicy(policy);
        appender.setEncoder(encoder);
        appender.start();

        logger.addAppender(appender);
        return logger;
    }

}
