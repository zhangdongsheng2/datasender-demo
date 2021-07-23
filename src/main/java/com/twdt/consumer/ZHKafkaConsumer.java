package com.twdt.consumer;

import com.twdt.receive.ConnectConfig;
import com.twdt.receive.ServerConnectorUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class ZHKafkaConsumer {

    public static void main(String[] args) {
        ConnectConfig config = new ConnectConfig();
        config.setTimeOutThreshold(ServerConnectorUtil.DEFAULT_TIME_THRESHOLD);
        config.setTag(ServerConnectorUtil.DEFAULT_CONSUMER_TAG);
        config.setHost("tw.twgiot.com");
        config.setPort(30007);
        ServerConnectorUtil.setup(config);


		sender();
    }

    @SuppressWarnings("resource")
    private static void sender() {
        Properties props = new Properties();
        props.put("bootstrap.servers", ConsumerConfig.servers);
        props.put("group.id", ConsumerConfig.groupId);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(ConsumerConfig.topics.split(",")));

        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(200);
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        ServerConnectorUtil.push("zh_ali_"+record.topic() + "★" + record.value());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
