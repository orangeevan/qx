package com.haipaite.common.ramcache.aop;

import com.haipaite.common.ramcache.lock.ChainLock;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aspect
public class LockAspect {
    private static final Logger logger = LoggerFactory.getLogger(LockAspect.class);


    private ConcurrentHashMap<Method, LockExtractor> extractors = new ConcurrentHashMap<>();


    @Around("@annotation(autoLocked)")
    public Object execute(ProceedingJoinPoint pjp, AutoLocked autoLocked) throws Throwable {
        Signature sign = pjp.getSignature();
        if (!(sign instanceof MethodSignature)) {
            logger.error("不支持的拦截切面:{}", sign);
            return pjp.proceed(pjp.getArgs());
        }


        Method method = ((MethodSignature) sign).getMethod();
        LockExtractor extractor = this.extractors.get(method);
        if (extractor == null) {
            extractor = createLockExtractor(method);
        }


        Object[] args = pjp.getArgs();
        ChainLock lock = extractor.getLock(args);
        if (lock == null) {
            return pjp.proceed(args);
        }

        lock.lock();
        try {
            return pjp.proceed(args);
        } finally {
            lock.unlock();
        }
    }


    private LockExtractor createLockExtractor(Method method) {
        LockExtractor result = LockExtractor.valueOf(method);
        LockExtractor prev = this.extractors.putIfAbsent(method, result);
        return (prev == null) ? result : prev;
    }
}