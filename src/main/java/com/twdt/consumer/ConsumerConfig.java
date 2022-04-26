package com.twdt.consumer;

import com.twdt.receive.ProducerConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConsumerConfig {

	public static Properties prop = new Properties();
	public static String location = "consumer-config.properties";

	public static String groupId;
	public static String servers;
	public static String topics;

	static {
		try {
			InputStream in = ProducerConfig.class.getClassLoader().getResourceAsStream(location);
			BufferedReader bf = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			prop.load(bf);
		} catch (IOException e) {
			e.printStackTrace();
		}

		groupId = prop.getProperty("groupId");
		servers = prop.getProperty("servers");
		topics = prop.getProperty("topic");
	}
}
