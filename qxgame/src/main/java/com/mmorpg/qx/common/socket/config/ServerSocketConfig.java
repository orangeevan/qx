package com.mmorpg.qx.common.socket.config;

import com.mmorpg.qx.common.logger.SysLoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * wnet.properties 管理
 * 
 * @author wangke
 * @since v1.0 2018年1月31日
 *
 */
@Component
public class ServerSocketConfig {

	private static final Logger logger = SysLoggerFactory.getLogger(ServerSocketConfig.class);
	private Properties prop = new Properties();

	@PostConstruct
	public void initProperties() throws IOException {
		String fileName = "socket.properties";

		InputStream inputStream = ServerSocketConfig.class.getClassLoader().getResourceAsStream(fileName);
		if (inputStream == null) {
			logger.error(String.format("没有找到socket.properties文件!"));
			throw new RuntimeException("没有找到socket.properties文件!");
		}
		prop.load(inputStream);
	}

	public String getProp(String key) {
		return prop.getProperty(key);
	}
}
