package com.twdt.receive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ProducerConfig {

	public static Properties prop = new Properties();
	public static String location = "producer-config.properties";

	public static String servers;
	public static String port;

	static {
		try {
			// 使用ClassLoader加载properties配置文件生成对应的输入流
			InputStream in = ProducerConfig.class.getClassLoader().getResourceAsStream(location);
			BufferedReader bf = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			prop.load(bf);
		} catch (IOException e) {
			e.printStackTrace();
		}

		servers = prop.getProperty("servers");
		port = prop.getProperty("port");
	}
}
