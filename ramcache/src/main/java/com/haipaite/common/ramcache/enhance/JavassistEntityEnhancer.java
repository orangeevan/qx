package com.haipaite.common.ramcache.enhance;

import com.haipaite.common.ramcache.IEntity;
import com.haipaite.common.ramcache.anno.ChkUnique;
import com.haipaite.common.ramcache.exception.ConfigurationException;
import com.haipaite.common.ramcache.exception.EnhanceException;
import com.haipaite.common.ramcache.service.EntityEnhanceService;
import com.haipaite.common.utility.ReflectionUtility;
import javassist.*;
import javassist.bytecode.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class JavassistEntityEnhancer
        implements Enhancer {
    private static final Logger logger = LoggerFactory.getLogger(JavassistEntityEnhancer.class);

    private static final ClassPool classPool = ClassPool.getDefault();


    private EntityEnhanceService enhanceService;


    private ConcurrentHashMap<Class, Constructor<? extends IEntity>> constructors = new ConcurrentHashMap<>();


    public void initialize(EntityEnhanceService enhanceService) {
        this.enhanceService = enhanceService;
    }

    @Override
    public <T extends IEntity> T transform(T entity) {
        Class<? extends IEntity> clz = (Class) entity.getClass();
        if (this.enhanceService == null) {
            FormattingTuple message = MessageFormatter.format("实体类[{}]所对应的缓存服务对象不存在", clz.getSimpleName());
            logger.error(message.getMessage());
            throw new EnhanceException(entity, message.getMessage());
        }
        try {
            Constructor<? extends IEntity> constructor = getConstructor(clz);
            return (T) constructor.newInstance(new Object[]{entity, this.enhanceService});
        } catch (Exception e) {
            FormattingTuple message = MessageFormatter.arrayFormat("实体类[{}]增强失败:{}", new Object[]{clz
                    .getSimpleName(), e.getMessage(), e});
            logger.error(message.getMessage());
            throw new EnhanceException(entity, message.getMessage());
        }
    }


    private <T extends IEntity> Constructor<T> getConstructor(Class<T> clz) throws Exception {
        if (this.constructors.containsKey(clz)) {
            return (Constructor<T>) this.constructors.get(clz);
        }

        synchronized (clz) {
            if (this.constructors.containsKey(clz)) {
                return (Constructor<T>) this.constructors.get(clz);
            }
            Class<T> current = createEnhancedClass(clz);
            Constructor<T> constructor = current.getConstructor(new Class[]{clz, EntityEnhanceService.class});
            this.constructors.put(clz, constructor);
            return constructor;
        }
    }

    private static HashSet<Method> objectMethods = new HashSet<>();

    static {
        for (Method m : Object.class.getDeclaredMethods()) {
            objectMethods.add(m);
        }
    }


    private Class createEnhancedClass(final Class clz) throws Exception {
        final CtClass enhancedClz = buildCtClass(clz);
        buildFields(clz, enhancedClz);
        buildConstructor(clz, enhancedClz);
        buildEnhancedEntityMethods(clz, enhancedClz);
        ReflectionUtility.doWithMethods(clz, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Enhance enhance = method.<Enhance>getAnnotation(Enhance.class);
                if (enhance == null) {

                    try {
                        JavassistEntityEnhancer.this.buildMethod(clz, enhancedClz, method);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("无法增强类[" + clz.getSimpleName() + "]的方法[" + method.getName() + "](桥接方法)", e);
                    }
                } else {
                    ChkUnique chkUnique = null;
                    int idx = 0;
                    Annotation[][] annos = method.getParameterAnnotations();
                    for (int i = 0; i < annos.length; i++) {
                        for (Annotation anno : annos[i]) {
                            if (anno instanceof ChkUnique) {
                                if (chkUnique != null) {
                                    throw new ConfigurationException("实体[" + clz.getName() + "]的方法[" + method.getName() + "]配置错误:唯一属性域不唯一");
                                }
                                chkUnique = (ChkUnique) anno;
                                idx = i;
                            }
                        }
                    }
                    if (chkUnique == null) {

                        try {
                            JavassistEntityEnhancer.this.buildEnhanceMethod(clz, enhancedClz, method, enhance);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("无法增强类[" + clz.getSimpleName() + "]的方法[" + method.getName() + "](普通的增强方法)", e);
                        }
                    } else {

                        try {
                            JavassistEntityEnhancer.this.buildEnhanceMethodWithUnique(clz, enhancedClz, method, enhance, chkUnique, idx);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("无法增强类[" + clz.getSimpleName() + "]的方法[" + method.getName() + "](带唯一属性域值验证的增强方法)", e);

                        }

                    }

                }

            }
        }, method -> objectMethods.contains(method) ? false : ((Modifier.isFinal(method.getModifiers()) || Modifier.isStatic(method.getModifiers()) || Modifier.isPrivate(method.getModifiers())) ? false : (


                !(method.isSynthetic() && method.getName().equals("getId")))));


        return enhancedClz.toClass();
    }


    private void buildEnhanceMethodWithUnique(Class clz, CtClass enhancedClz, Method method, Enhance enhance, ChkUnique chkUnique, int idx) throws Exception {
        if (StringUtils.isNotEmpty(enhance.value())) {
            throw new ConfigurationException("不支持的增强方法配置");
        }
        if (!enhance.ignore().equals(void.class)) {
            throw new ConfigurationException("不支持的增强方法配置");
        }


        Class<?> returnType = method.getReturnType();


        CtMethod ctMethod = new CtMethod(classPool.get(returnType.getCanonicalName()), method.getName(), toCtClassArray(method.getParameterTypes()), enhancedClz);
        ctMethod.setModifiers(1);
        if ((method.getExceptionTypes()).length != 0) {
            ctMethod.setExceptionTypes(toCtClassArray(method.getExceptionTypes()));
        }


        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("{com.my9yu.common.ramcache.anno.CachedEntityConfig config = service.getEntityConfig();java.util.concurrent.locks.Lock lock = config.getUniqueWriteLock(\"" + chkUnique


                .value() + "\");lock.lock();try {if (" + "service" + ".hasUniqueValue(\"" + chkUnique


                .value() + "\", $" + (idx + 1) + ")) {throw new " + "com.my9yu.common.ramcache.exception.UniqueFieldException" + "(\"唯一属性[" + chkUnique
                .value() + "]值[\" + $" + (idx + 1) + " + \"]已经存在\");}");

        if (returnType == void.class) {
            bodyBuilder.append("entity." + method.getName() + "($$);" + "service" + ".replaceUniqueValue(entity.getId(), \"" + chkUnique
                    .value() + "\", $" + (idx + 1) + ");" + "service" + ".writeBack(entity.getId(), entity);} finally {");
        } else {

            bodyBuilder.append(returnType.getName() + " result = " + "entity" + "." + method.getName() + "($$);" + "service" + ".replaceUniqueValue(" + "entity" + ".getId(), \"" + chkUnique
                    .value() + "\", $" + (idx + 1) + ");" + "service" + ".writeBack(entity.getId(), entity);return result;} finally {");
        }


        bodyBuilder.append("lock.unlock();}}");


        ctMethod.setBody(bodyBuilder.toString());
        enhancedClz.addMethod(ctMethod);
    }


    private void buildEnhanceMethod(Class clz, CtClass enhancedClz, Method method, Enhance enhance) throws Exception {
        Class<?> returnType = method.getReturnType();


        CtMethod ctMethod = new CtMethod(classPool.get(returnType.getCanonicalName()), method.getName(), toCtClassArray(method.getParameterTypes()), enhancedClz);
        ctMethod.setModifiers(1);
        if ((method.getExceptionTypes()).length != 0) {
            ctMethod.setExceptionTypes(toCtClassArray(method.getExceptionTypes()));
        }

        if (returnType == void.class) {


            ctMethod.setBody("{entity." + method
                    .getName() + "($$);" + "service" + ".writeBack(entity.getId(), entity);}");

        } else if (StringUtils.isBlank(enhance.value())) {


            ctMethod.setBody("{" + returnType
                    .getName() + " result =" + "entity" + "." + method
                    .getName() + "($$);" + "service" + ".writeBack(entity.getId(), entity);return result;}");


        } else {


            ctMethod.setBody("{" + method
                    .getReturnType().getName() + " result = " + "entity" + "." + method.getName() + "($$);if (String.valueOf(result).equals(\"" + enhance
                    .value() + "\")) {" + "service" + ".writeBack(entity.getId(), entity);}return result;}");
        }


        if (!enhance.ignore().equals(void.class)) {
            CtClass etype = classPool.get(enhance.ignore().getCanonicalName());


            ctMethod.addCatch("{service.writeBack(entity.getId(), entity);throw $e;}", etype);
        }


        enhancedClz.addMethod(ctMethod);
    }


    private void buildMethod(Class clz, CtClass enhancedClz, Method method) throws Exception {
        Class<?> returnType = method.getReturnType();


        CtMethod ctMethod = new CtMethod(classPool.get(returnType.getCanonicalName()), method.getName(), toCtClassArray(method.getParameterTypes()), enhancedClz);
        ctMethod.setModifiers(method.getModifiers());
        if ((method.getExceptionTypes()).length != 0) {
            ctMethod.setExceptionTypes(toCtClassArray(method.getExceptionTypes()));
        }


        if (returnType == void.class) {

            ctMethod.setBody("{entity." + method
                    .getName() + "($$);}");
        } else {

            ctMethod.setBody("{return entity." + method
                    .getName() + "($$);}");
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
        CtField field = new CtField(classPool.get(EntityEnhanceService.class.getCanonicalName()), "service", enhancedClz);

        field.setModifiers(18);
        enhancedClz.addField(field);

        field = new CtField(classPool.get(entityClz.getCanonicalName()), "entity", enhancedClz);
        field.setModifiers(18);
        enhancedClz.addField(field);
    }


    private void buildConstructor(Class entityClz, CtClass enhancedClz) throws Exception {
        CtConstructor constructor = new CtConstructor(toCtClassArray(entityClz, EntityEnhanceService.class), enhancedClz);
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
        methodInfo.addAttribute((AttributeInfo) annoAttr);

        CtMethod method = CtMethod.make(methodInfo, enhancedClz);


        method.setBody("{return this.entity;}");


        method.setModifiers(1);
        enhancedClz.addMethod(method);
    }


    private CtClass[] toCtClassArray(Class<?>... classes) throws NotFoundException {
        if (classes == null || classes.length == 0) {
            return new CtClass[0];
        }
        CtClass[] result = new CtClass[classes.length];
        for (int i = 0; i < classes.length; i++) {
            result[i] = classPool.get(classes[i].getCanonicalName());
        }
        return result;
    }
}


/* Location:              C:\Users\ShBy\Desktop\ss\haipaite-ramcache-1.0.1.jar!\com\haipaite\common\ramcache\enhance\JavassistEntityEnhancer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */