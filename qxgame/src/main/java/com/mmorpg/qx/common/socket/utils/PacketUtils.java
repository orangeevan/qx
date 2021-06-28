package com.mmorpg.qx.common.socket.utils;

import com.baidu.bjf.remoting.protobuf.ProtobufIDLGenerator;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.haipaite.common.utility.FileUtils;
import com.mmorpg.qx.common.module.Module;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author wang ke
 * @description:
 * @since 14:17 2020-08-10
 */
public class PacketUtils {
    private static void iDLGenerator(Class<?> clazz, short packetId, String protoDir, String protoClientDir) throws Exception {
        if (StringUtils.isBlank(protoDir)) {
            return;
        }
        String idl = ProtobufIDLGenerator.getIDL(clazz, null, null, true);
        File file = new File(protoDir + packetId + "_" + clazz.getSimpleName() + ".proto");
        //如有修改老协议，把老协议文件先删除再生成
        // if(!file.exists()) {
        FileUtils.createFile(file);
        FileWriter fw = new FileWriter(file);
        fw.write(idl);
        fw.flush();
        fw.close();
        //}
        File fileClient = new File(protoClientDir + clazz.getSimpleName() + "_" + packetId + ".proto");
        //如有修改老协议，把老协议文件先删除再生成
        // if(!file.exists()) {
        FileUtils.createFile(fileClient);
        FileWriter fwClient = new FileWriter(fileClient);
        fwClient.write(idl);
        fwClient.flush();
        fwClient.close();

        //}
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext_test.xml");
        Resource resource = applicationContext.getResource("inner.properties");
        try {
            String packetPath = null;
            String packetClientPath = null;
            Map<Short, String> packetIds = new HashMap<>();
            if (resource.isReadable()) {
                Properties props = new Properties();
                props.load(resource.getInputStream());
                packetPath = props.getProperty("server.config.packetPath");
                packetClientPath = props.getProperty("server.config.packetClientPath");
            }
            String[] className = applicationContext.getBeanNamesForAnnotation(SocketPacket.class);
            if (ArrayUtils.isNotEmpty(className)) {
                Map packetDatas = new HashMap<Short, String>();
                for (String name : className) {
                    Class<?> packetClass = applicationContext.getType(name);
                    SocketPacket annotation = packetClass.getAnnotation(SocketPacket.class);
                    //检验
                    try {
                        ProtobufProxy.create(packetClass);
                    } catch (IllegalArgumentException e) {
                        //e.printStackTrace();
                    }
                    Object o = packetClass.newInstance();
                    short packetid = (short) (annotation.module() > 0 ? annotation.module() * Module.MAX + annotation.packetId() : annotation.packetId());
                    if (packetIds.containsKey(packetid)) {
                        throw new IllegalArgumentException(String.format("消息ID [%d]重复使用 [%s] [%s]", annotation.packetId(), packetIds.get(annotation.packetId()), name));
                    }
                    packetIds.put(packetid, name);
                    iDLGenerator(packetClass, packetid, packetPath, packetClientPath);
                    packetDatas.put(packetid, name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
