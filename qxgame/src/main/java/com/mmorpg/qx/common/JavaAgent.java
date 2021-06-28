package com.mmorpg.qx.common;

import com.bojoy.agent.JavaDynAgent;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


public class JavaAgent {

    public static final Logger logger = SysLoggerFactory.getLogger(JavaAgent.class);

    private static String classesPath;
    private static String jarPath;
    private static VirtualMachine vm;
    private static String pid;

    static {
        classesPath = JavaAgent.class.getClassLoader().getResource("").getPath();
        logger.error("java agent:classpath:{}", classesPath);
        jarPath = getJarPath();
        logger.error("java agent:jarPath:{}", jarPath);
        System.err.println("java agent:jarPath: " + jarPath);
        // 当前进程pid
        String name = ManagementFactory.getRuntimeMXBean().getName();
        pid = name.split("@")[0];
        logger.error("当前进程pid：{}", pid);
    }

    /**
     * 获取jar包路径
     *
     * @return
     */
    public static String getJarPath() {
        URL url = JavaDynAgent.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith("agent-2.5.jar")) {// 可执行jar包运行的结果里包含".jar"
            // 截取路径中的jar包名
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }

        File file = new File(filePath);

        filePath = file.getAbsolutePath();//得到绝对路径
        return filePath;
    }

    private static void init() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        // 虚拟机加载
        vm = VirtualMachine.attach(pid);
        vm.loadAgent(jarPath + "/agent-2.5.jar");
        Instrumentation instrumentation = JavaDynAgent.getInstrumentation();
        if (instrumentation == null) {
            throw new RuntimeException("initInstrumentation must not be null");
        }
    }

    private static void destroy() throws IOException {
        if (vm != null) {
            vm.detach();
        }
    }

    /**
     * 重新加载类
     *
     * @param classArr
     * @throws Exception
     */
    public static void javaAgent(String root, String[] classArr) throws ClassNotFoundException, IOException, UnmodifiableClassException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        init();
        try {
            // 1.整理需要重定义的类
            List<ClassDefinition> classDefList = new ArrayList<ClassDefinition>();
            for (String className : classArr) {
                className = className.substring(0, className.indexOf(".class"));
                Class<?> c = Class.forName(className);
                String classPath = (StringUtils.isNotBlank(root) ? root : classesPath) + className.replace(".", "/") + ".class";
                //String classPath = (StringUtils.isNotBlank(root) ? root : classesPath) + className;
                logger.error("class redefined:" + classPath);
                //byte[] bytesFromFile = Files.toByteArray(new File(classPath));
                byte[] bytesFromFile = FileUtils.readFileToByteArray(new File(classPath));
                ClassDefinition classDefinition = new ClassDefinition(c, bytesFromFile);
                classDefList.add(classDefinition);
            }
            // 2.redefine
            JavaDynAgent.getInstrumentation().redefineClasses(classDefList.toArray(new ClassDefinition[classDefList.size()]));
        } finally {
            destroy();
        }
    }
}
