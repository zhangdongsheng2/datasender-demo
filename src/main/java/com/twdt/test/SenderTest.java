package com.twdt.test;


import com.twdt.sender.ConnectConfig;
import com.twdt.sender.ServerConnectorUtil;

public class SenderTest {

    public static void main(String[] args) {
        ConnectConfig config = new ConnectConfig();
        config.setTimeOutThreshold(180000L);
        config.setTag("DEFAULT");
        config.setHost("localhost");
        config.setPort(6000);
        ServerConnectorUtil.setup(config);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException var4) {
            var4.printStackTrace();
        }

        Thread producer = new Thread(new Runnable() {
            public void run() {
                ServerConnectorUtil.push("RUNTIME", "runtime-start");
                ServerConnectorUtil.push("RUNTIME", "runtime-start");
            }
        });
        producer.setName("生产者线程");
        producer.start();

        while(true) {
            while(true) {
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException var5) {
                    var5.printStackTrace();
                }
            }
        }
    }
}
