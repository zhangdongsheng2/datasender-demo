package com.twdt.receive;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消费者队列, 数据在队列中, 不断的发送到服务端.
 */
public class QueueConsumer extends Thread {
    private final Logger logger = LoggerFactory.getLogger(QueueConsumer.class);
    private final ConnectConfig config;
    private final LinkedBlockingQueue<String> queue;
    boolean isConnected = false;
    private Socket socket = null;
    private BufferedWriter bw = null;
    private final Timer timer;
    QueueGCTask gcTask = new QueueGCTask(this);
    QueueConsumer.ReConnectTask reConnectTask = new QueueConsumer.ReConnectTask();
    CountTask countTask = new CountTask(this);

    public ConnectConfig getConfig() {
        return this.config;
    }

    public LinkedBlockingQueue<String> getQueue() {
        return this.queue;
    }

    public QueueConsumer(ConnectConfig config) {
        this.config = config;
        //创建定时任务
        this.timer = new Timer("Timer:" + config.getTag() + "->" + config.getHost() + ":" + config.getPort(), true);
        //创建默认队列
        this.queue = new LinkedBlockingQueue<>(config.getQueueSize());
        //每隔5秒检查连接是否断开了, 如果断开就重写连接.
        this.timer.schedule(this.reConnectTask, 10000L, 5000L);
        //每隔10秒遍历查询队列是否超过了
        this.timer.schedule(this.gcTask, 10000L, 10000L);
        //从下一分钟开始, 每隔一分钟统计一次 Count
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + 1);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        this.timer.schedule(this.countTask, c.getTime(), 60000L);
    }


    public void run() {
        while (true) {
            //如果未连接到服务, 就等待1秒钟.
            if (!this.isConnected) {
                try {
                    Thread.sleep(1000L);
                    continue;
                } catch (Exception e) {
                    this.logger.debug("", e);
                }
            }

            String data = null;
            try {
                //从队列头部移除一个数据, 发送.
                data = this.queue.take();
            } catch (Exception ignored) {
            }

            if (data != null) {
                this.send2Server(data);
                //记录一下总发送条数
                this.config.setSendCount(this.config.getSendCount() + 1L);
            }
        }
    }


    //发送数据
    public boolean push(Object object) {
        if (object == null) return false;

        this.config.setPushCount(this.config.getPushCount() + 1L);
        //队列的尾部插入数据.
        return this.queue.offer(object.toString());
    }


    //发送数据到服务端
    void send2Server(String data) {
        try {
            if (this.isConnected) {
                this.bw.write(data);
                this.bw.newLine();
                this.bw.flush();
            }
        } catch (Exception e) {
            //数据发送失败端口重连Socket
            this.logger.error("send data fail!", e);
            if (this.isConnected) {
                try {
                    this.socket.close();
                } catch (Exception ignored) {
                }
            }
            this.isConnected = false;
        }

    }

    //停止队列线程, 停止所有定时任务, 关闭socket连接.
    public void stopConsumer() {
        this.gcTask.cancel();
        this.reConnectTask.cancel();
        this.countTask.cancel();
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.timer.cancel();
        this.interrupt();
    }


    //定时检查启动客户端, 建立链接
    class ReConnectTask extends TimerTask {
        public void run() {
            if (!QueueConsumer.this.isConnected) {
                //创建Socket连接.
                try {
                    QueueConsumer.this.socket = new Socket(QueueConsumer.this.config.getHost(), QueueConsumer.this.config.getPort());
                    QueueConsumer.this.socket.setTcpNoDelay(true);
                    QueueConsumer.this.socket.setSoTimeout(10000);
                    QueueConsumer.this.bw = new BufferedWriter(new OutputStreamWriter(QueueConsumer.this.socket.getOutputStream(), StandardCharsets.UTF_8));

                    QueueConsumer.this.queue.clear();
                    QueueConsumer.this.isConnected = true;
                } catch (Exception var2) {
                    QueueConsumer.this.isConnected = false;
                    QueueConsumer.this.logger.error(String.format("connect to %s->%s:%d fail!", QueueConsumer.this.config.getTag(), QueueConsumer.this.config.getHost(), QueueConsumer.this.config.getPort()), var2);
                }
            }

        }
    }
}