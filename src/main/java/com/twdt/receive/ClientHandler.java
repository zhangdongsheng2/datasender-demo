package com.twdt.receive;


import com.twdt.sender.ServerConnectorUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 客户端接入监听类, 收到数据发送到Kafka
 **/
public class ClientHandler {
    private static KafkaProducer<String,String> producer;

    /** 数据连接值 */
    private final Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
        Properties props = new Properties();
        props.put("bootstrap.servers", ProducerConfig.servers);
        props.put("buffer.memory", 67108864);//默认 33554432 = 32M * 1024 * 1024
        props.put("acks", "1");  //0 1 (-1 or all)
        props.put("retries", 3); //重试次数
        props.put("retry.backoff.ms", 500);//重试等待毫秒
        props.put("batch.size", 163840);
        props.put("linger.ms", 100); //稍微延迟增加吞吐量
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //设置分区类,根据key进行数据分区
        producer = new KafkaProducer<>(props);

    }

    public void start(){
        System.out.println("新客户端接入");
        new Thread(new Runnable() {
            @Override
            public void run() {
                doStart();
            }
        }).start();
    }

    /**
     * 对客户端的业务处理，接收并重写回去
     */
    private void doStart(){
        try {
            InputStream inputStream = socket.getInputStream();
            while (true){ //不停的接收数据
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String message;

                while((message = bufferedReader.readLine()) != null) {
                    if (message.contains("★")) {
                        String[] topicAndValue = message.split("★");
                        try {
                            producer.send(new ProducerRecord<>(topicAndValue[0], topicAndValue[1].trim()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        //统计日志信息.
                        ServerConnectorUtil.write2Log(message);
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}