//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.haipaite.common.event.util;

import com.haipaite.common.event.core.IReceiverInvoke;
import com.haipaite.common.event.core.ReceiverDefintion;
import com.haipaite.common.event.event.IEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;

public final class EnhanceUtil {
  private static final ClassPool classPool = ClassPool.getDefault();
  private static final AtomicInteger index = new AtomicInteger(0);

  public EnhanceUtil() {
  }

  public static IReceiverInvoke createReceiverInvoke(ReceiverDefintion defintion) throws Exception {
    Object bean = defintion.getBean();
    Method method = defintion.getMethod();
    String methodName = method.getName();
    Class<?> clz = bean.getClass();
    CtClass enhancedClz = buildCtClass(IReceiverInvoke.class);
    CtField field = new CtField(classPool.get(clz.getCanonicalName()), "bean", enhancedClz);
    field.setModifiers(2);
    enhancedClz.addField(field);
    CtConstructor constructor = new CtConstructor(classPool.get(new String[]{clz.getCanonicalName()}), enhancedClz);
    constructor.setBody("{this.bean = $1;}");
    constructor.setModifiers(1);
    enhancedClz.addConstructor(constructor);
    CtMethod ctMethod = new CtMethod(classPool.get(Void.TYPE.getCanonicalName()), "invoke", classPool.get(new String[]{IEvent.class.getCanonicalName()}), enhancedClz);
    ctMethod.setModifiers(17);
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append(" bean." + methodName + "((" + defintion.getClz().getCanonicalName() + ")$1);");
    sb.append("}");
    ctMethod.setBody(sb.toString());
    enhancedClz.addMethod(ctMethod);
    CtMethod ectMethod = new CtMethod(classPool.get(Boolean.TYPE.getCanonicalName()), "equals", classPool.get(new String[]{Object.class.getCanonicalName()}), enhancedClz);
    ectMethod.setModifiers(1);
    sb = new StringBuilder();
    sb.append("{");
    sb.append("com.haipaite.common.event.core.IReceiverInvoke other = (com.haipaite.common.event.core.IReceiverInvoke) $1;");
    sb.append("return bean.equals(other.getBean());");
    sb.append("}");
    ectMethod.setBody(sb.toString());
    enhancedClz.addMethod(ectMethod);
    CtMethod hctMethod = new CtMethod(classPool.get(Integer.TYPE.getCanonicalName()), "hashCode", classPool.get(new String[0]), enhancedClz);
    hctMethod.setModifiers(1);
    sb = new StringBuilder();
    sb.append("{");
    sb.append(" return bean.hashCode();");
    sb.append("}");
    hctMethod.setBody(sb.toString());
    enhancedClz.addMethod(hctMethod);
    CtMethod gctMethod = new CtMethod(classPool.get(Object.class.getCanonicalName()), "getBean", classPool.get(new String[0]), enhancedClz);
    gctMethod.setModifiers(1);
    sb = new StringBuilder();
    sb.append("{");
    sb.append("return bean;");
    sb.append("}");
    gctMethod.setBody(sb.toString());
    enhancedClz.addMethod(gctMethod);
    Class<?> rClz = enhancedClz.toClass();
    Constructor<?> con = rClz.getConstructor(clz);
    IReceiverInvoke result = (IReceiverInvoke)con.newInstance(bean);
    return result;
  }

  private static CtClass buildCtClass(Class<?> clz) throws Exception {
    CtClass result = classPool.makeClass(clz.getSimpleName() + "Enhance" + index.incrementAndGet());
    result.addInterface(classPool.get(clz.getCanonicalName()));
    return result;
  }
}
