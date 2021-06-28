package com.haipaite.common.ramcache.aop;

import com.haipaite.common.ramcache.lock.ChainLock;
import com.haipaite.common.ramcache.lock.LockUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LockExtractor {
    private static final Logger logger = LoggerFactory.getLogger(LockExtractor.class);


    public static LockExtractor valueOf(Method method) {
        LockExtractor result = new LockExtractor();
        Annotation[][] annos = method.getParameterAnnotations();
        for (int i = 0; i < annos.length; i++) {
            IsLocked isLocked = null;
            for (Annotation anno : annos[i]) {
                if (anno instanceof IsLocked) {
                    isLocked = (IsLocked) anno;
                    break;
                }
            }
            if (isLocked != null) {
                result.lockArgs.put(Integer.valueOf(i), isLocked);
            }
        }
        return result;
    }

    private HashMap<Integer, IsLocked> lockArgs = new HashMap<>();


    public ChainLock getLock(Object[] args) {
        ArrayList<Object> lockObjs = new ArrayList();

        for (Map.Entry<Integer, IsLocked> entry : this.lockArgs.entrySet()) {
            IsLocked isLocked = entry.getValue();
            Object arg = args[((Integer) entry.getKey()).intValue()];
            if (arg == null) {
                continue;
            }

            if (!isLocked.element()) {
                lockObjs.add(arg);

                continue;
            }
            if (arg instanceof java.util.Collection) {
                Collection var7 = ((Collection)arg);
                for (Object obj : var7) {
                    if (obj == null) {
                        continue;
                    }
                    lockObjs.add(obj);
                }

                continue;
            }
            if (arg.getClass().isArray()) {
                for (int i = 0; i < Array.getLength(arg); i++) {
                    Object obj = Array.get(arg, i);
                    if (obj != null) {

                        lockObjs.add(obj);
                    }
                }
                continue;
            }
            if (arg instanceof Map) {
                for (Object obj : ((Map) arg).values()) {
                    if (obj == null) {
                        continue;
                    }
                    lockObjs.add(obj);
                }

                continue;
            }
            logger.error("不支持的类型[{}]", arg.getClass().getName());
        }

        if (lockObjs.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("没有获得锁目标");
            }
            return null;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("锁目标为:{}", Arrays.toString(lockObjs.toArray()));
        }
        return LockUtils.getLock(lockObjs.toArray());
    }
}