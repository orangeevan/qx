//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.haipaite.common.ramcache.enhance;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.ChangeIndex;
import com.haipaite.common.ramcache.exception.ConfigurationException;
import com.haipaite.common.ramcache.exception.EnhanceException;
import com.haipaite.common.ramcache.service.RegionEnhanceService;
import com.haipaite.common.utility.ReflectionUtility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

public class JavassistRegionEnhancer implements Enhancer {
    private static final Logger logger = LoggerFactory.getLogger(JavassistRegionEnhancer.class);
    private static final ClassPool classPool = ClassPool.getDefault();
    private RegionEnhanceService enhanceService;
    private ConcurrentHashMap<Class, Constructor<? extends IEntity>> constructors = new ConcurrentHashMap();
    private static HashSet<Method> objectMethods = new HashSet();

    public JavassistRegionEnhancer() {
    }

    public void initialize(RegionEnhanceService enhanceService) {
        this.enhanceService = enhanceService;
    }

    @Override
    public <T extends IEntity> T transform(T entity) {
        Class<? extends IEntity> clz = entity.getClass();
        if (this.enhanceService == null) {
            FormattingTuple message = MessageFormatter.format("实体类[{}]所对应的缓存服务对象不存在", clz.getSimpleName());
            logger.error(message.getMessage());
            throw new EnhanceException(entity, message.getMessage());
        } else {
            try {
                Constructor constructor = this.getConstructor(clz);
                return (T) constructor.newInstance(entity, this.enhanceService);
            } catch (Exception var5) {
                FormattingTuple message = MessageFormatter.arrayFormat("实体类[{}]增强失败:{}", new Object[]{clz.getSimpleName(), var5.getMessage(), var5});
                logger.error(message.getMessage());
                throw new EnhanceException(entity, message.getMessage());
            }
        }
    }

    private <T extends IEntity> Constructor<T> getConstructor(Class<T> clz) throws Exception {
        if (this.constructors.containsKey(clz)) {
            return (Constructor) this.constructors.get(clz);
        } else {
            synchronized (clz) {
                if (this.constructors.containsKey(clz)) {
                    return (Constructor) this.constructors.get(clz);
                } else {
                    Class current = this.createEnhancedClass(clz);
                    Constructor<T> constructor = current.getConstructor(clz, RegionEnhanceService.class);
                    this.constructors.put(clz, constructor);
                    return constructor;
                }
            }
        }
    }

    private Class createEnhancedClass(final Class clz) throws Exception {
        final CtClass enhancedClz = this.buildCtClass(clz);
        this.buildFields(clz, enhancedClz);
        this.buildConstructor(clz, enhancedClz);
        this.buildEnhancedEntityMethods(clz, enhancedClz);
        ReflectionUtility.doWithMethods(clz, new MethodCallback() {
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Enhance enhance = (Enhance) method.getAnnotation(Enhance.class);
                if (enhance == null) {
                    try {
                        JavassistRegionEnhancer.this.buildMethod(clz, enhancedClz, method);
                    } catch (Exception var13) {
                        throw new IllegalArgumentException("无法增强类[" + clz.getSimpleName() + "]的方法[" + method.getName() + "](桥接方法)", var13);
                    }
                } else {
                    ChangeIndex changeIndex = null;
                    int idx = 0;
                    Annotation[][] annos = method.getParameterAnnotations();

                    for (int i = 0; i < annos.length; ++i) {
                        Annotation[] var7 = annos[i];
                        int var8 = var7.length;

                        for (int var9 = 0; var9 < var8; ++var9) {
                            Annotation anno = var7[var9];
                            if (anno instanceof ChangeIndex) {
                                if (changeIndex != null) {
                                    throw new ConfigurationException("实体[" + clz.getName() + "]的方法[" + method.getName() + "]配置错误:索引属性域不唯一");
                                }

                                changeIndex = (ChangeIndex) anno;
                                idx = i;
                            }
                        }
                    }

                    if (changeIndex == null) {
                        try {
                            JavassistRegionEnhancer.this.buildEnhanceMethod(clz, enhancedClz, method, enhance);
                        } catch (Exception var12) {
                            throw new IllegalArgumentException("无法增强类[" + clz.getSimpleName() + "]的方法[" + method.getName() + "](普通的增强方法)", var12);
                        }
                    } else {
                        try {
                            JavassistRegionEnhancer.this.buildEnhanceMethodWithChangeIndex(clz, enhancedClz, method, enhance, changeIndex, idx);
                        } catch (Exception var11) {
                            throw new IllegalArgumentException("无法增强类[" + clz.getSimpleName() + "]的方法[" + method.getName() + "](带更改索引属性值的增强方法)", var11);
                        }
                    }
                }

            }
        }, new MethodFilter() {
            @Override
            public boolean matches(Method method) {
                if (JavassistRegionEnhancer.objectMethods.contains(method)) {
                    return false;
                } else if (!Modifier.isFinal(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()) && !Modifier.isPrivate(method.getModifiers())) {
                    return !method.isSynthetic() || !method.getName().equals("getId");
                } else {
                    return false;
                }
            }
        });
        return enhancedClz.toClass();
    }

    private void buildEnhanceMethodWithChangeIndex(Class clz, CtClass enhancedClz, Method method, Enhance enhance, ChangeIndex changeIndex, int idx) throws Exception {
        if (StringUtils.isNotEmpty(enhance.value())) {
            throw new ConfigurationException("不支持的增强方法配置");
        } else if (!enhance.ignore().equals(Void.TYPE)) {
            throw new ConfigurationException("不支持的增强方法配置");
        } else {
            Class<?> returnType = method.getReturnType();
            CtMethod ctMethod = new CtMethod(classPool.get(returnType.getCanonicalName()), method.getName(), this.toCtClassArray(method.getParameterTypes()), enhancedClz);
            ctMethod.setModifiers(1);
            if (method.getExceptionTypes().length != 0) {
                ctMethod.setExceptionTypes(this.toCtClassArray(method.getExceptionTypes()));
            }

            StringBuilder bodyBuilder = new StringBuilder();
            bodyBuilder.append("{com.my9yu.common.ramcache.anno.CachedEntityConfig config = service.getEntityConfig();Object prev = config.getIndexValue(\"" + changeIndex.value() + "\", this);");
            if (returnType == Void.TYPE) {
                bodyBuilder.append("entity." + method.getName() + "($$);" + "service" + ".changeIndexValue(\"" + changeIndex.value() + "\", this, prev);" + "service" + ".writeBack(entity.getId(), entity);}");
            } else {
                bodyBuilder.append(returnType.getName() + " result = " + "entity" + "." + method.getName() + "($$);" + "service" + ".changeIndexValue(\"" + changeIndex.value() + "\", this, prev);" + "service" + ".writeBack(entity.getId(), entity);return result;}");
            }

            ctMethod.setBody(bodyBuilder.toString());
            enhancedClz.addMethod(ctMethod);
        }
    }

    private void buildEnhanceMethod(Class clz, CtClass enhancedClz, Method method, Enhance enhance) throws Exception {
        Class<?> returnType = method.getReturnType();
        CtMethod ctMethod = new CtMethod(classPool.get(returnType.getCanonicalName()), method.getName(), this.toCtClassArray(method.getParameterTypes()), enhancedClz);
        ctMethod.setModifiers(1);
        if (method.getExceptionTypes().length != 0) {
            ctMethod.setExceptionTypes(this.toCtClassArray(method.getExceptionTypes()));
        }

        if (returnType == Void.TYPE) {
            ctMethod.setBody("{entity." + method.getName() + "($$);" + "service" + ".writeBack(entity.getId(), entity);}");
        } else if (StringUtils.isBlank(enhance.value())) {
            ctMethod.setBody("{" + returnType.getName() + " result =" + "entity" + "." + method.getName() + "($$);" + "service" + ".writeBack(entity.getId(), entity);return result;}");
        } else {
            ctMethod.setBody("{" + method.getReturnType().getName() + " result = " + "entity" + "." + method.getName() + "($$);if (String.valueOf(result).equals(\"" + enhance.value() + "\")) {" + "service" + ".writeBack(entity.getId(), entity);}return result;}");
        }

        if (!enhance.ignore().equals(Void.TYPE)) {
            CtClass etype = classPool.get(enhance.ignore().getCanonicalName());
            ctMethod.addCatch("{service.writeBack(entity.getId(), entity);throw $e;}", etype);
        }

        enhancedClz.addMethod(ctMethod);
    }

    private void buildMethod(Class clz, CtClass enhancedClz, Method method) throws Exception {
        Class<?> returnType = method.getReturnType();
        CtMethod ctMethod = new CtMethod(classPool.get(returnType.getCanonicalName()), method.getName(), this.toCtClassArray(method.getParameterTypes()), enhancedClz);
        ctMethod.setModifiers(method.getModifiers());
        if (method.getExceptionTypes().length != 0) {
            ctMethod.setExceptionTypes(this.toCtClassArray(method.getExceptionTypes()));
        }

        if (returnType == Void.TYPE) {
            ctMethod.setBody("{entity." + method.getName() + "($$);}");
        } else {
            ctMethod.setBody("{return entity." + method.getName() + "($$);}");
        }

        enhancedClz.addMethod(ctMethod);
    }

    private CtClass buildCtClass(Class entityClz) throws Exception {
        CtClass superClz = classPool.get(entityClz.getCanonicalName());
        CtClass result = classPool.makeClass(entityClz.getCanonicalName() + "$ENHANCED");
        result.setSuperclass(superClz);
        result.setInterfaces(new CtClass[]{classPool.get(EnhancedEntity.class.getCanonicalName())});
        return result;
    }

    private void buildFields(Class entityClz, CtClass enhancedClz) throws Exception {
        CtField field = new CtField(classPool.get(RegionEnhanceService.class.getCanonicalName()), "service", enhancedClz);
        field.setModifiers(18);
        enhancedClz.addField(field);
        field = new CtField(classPool.get(entityClz.getCanonicalName()), "entity", enhancedClz);
        field.setModifiers(18);
        enhancedClz.addField(field);
    }

    private void buildConstructor(Class entityClz, CtClass enhancedClz) throws Exception {
        CtConstructor constructor = new CtConstructor(this.toCtClassArray(entityClz, RegionEnhanceService.class), enhancedClz);
        constructor.setBody("{ this.entity = $1; this.service = $2;}");
        constructor.setModifiers(1);
        enhancedClz.addConstructor(constructor);
    }

    private void buildEnhancedEntityMethods(Class entityClass, CtClass enhancedClz) throws Exception {
        CtClass returnType = classPool.get(IEntity.class.getCanonicalName());
        String mname = "getEntity";
        CtClass[] parameters = new CtClass[0];
        ConstPool cp = enhancedClz.getClassFile2().getConstPool();
        String desc = Descriptor.ofMethod(returnType, parameters);
        MethodInfo methodInfo = new MethodInfo(cp, mname, desc);
        AnnotationsAttribute annoAttr = new AnnotationsAttribute(cp, "RuntimeVisibleAnnotations");
        javassist.bytecode.annotation.Annotation annot = new javassist.bytecode.annotation.Annotation("org.codehaus.jackson.annotate.JsonIgnore", cp);
        annoAttr.addAnnotation(annot);
        methodInfo.addAttribute(annoAttr);
        CtMethod method = CtMethod.make(methodInfo, enhancedClz);
        method.setBody("{return this.entity;}");
        method.setModifiers(1);
        enhancedClz.addMethod(method);
    }

    private CtClass[] toCtClassArray(Class<?>... classes) throws NotFoundException {
        if (classes != null && classes.length != 0) {
            CtClass[] result = new CtClass[classes.length];

            for (int i = 0; i < classes.length; ++i) {
                result[i] = classPool.get(classes[i].getCanonicalName());
            }

            return result;
        } else {
            return new CtClass[0];
        }
    }

    static {
        Method[] var0 = Object.class.getDeclaredMethods();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2) {
            Method m = var0[var2];
            objectMethods.add(m);
        }

    }
}
