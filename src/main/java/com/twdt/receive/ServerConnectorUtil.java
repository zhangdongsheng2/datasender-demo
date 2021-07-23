package com.twdt.receive;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerConnectorUtil {
    private static final Logger logger = LoggerFactory.getLogger(ServerConnectorUtil.class);
    private static final Map<String, QueueConsumer> consumers = new HashMap<>();
    //默认队列名
    public static final String DEFAULT_CONSUMER_TAG = "DEFAULT";
    //队列最大数据
    public static final int DEFAULT_MAX_QUEUE_SIZE = 10000;
    //队列 清理的 阈值
    public static float DEFAULT_QUEUE_GC_THRESHOLD = 0.8F;
    //默认超时时间
    public static final long DEFAULT_TIME_THRESHOLD = 180000L;
    //是否启用超时
    public static final long DEFAULT_TIME_THRESHOLD_OFF = 0L;


    //添加数据到发送队列.
    public static boolean push(Object object) {
        String tag = null;
        if (consumers.size() == 1) {
            tag = (String) consumers.keySet().toArray()[0];
            return push(tag, object);
        } else {
            logger.error("has more than 1 consumers " + Arrays.toString(consumers.keySet().toArray()) + ",use ServerConnector.push(tag,object) methon to push message");
            return false;
        }
    }

    //根据Tag添加数据到发送队列
    public static boolean push(String tag, Object object) {
        if (tag == null || tag.trim().isEmpty()) {
            tag = DEFAULT_CONSUMER_TAG;
        }

        QueueConsumer consumer = consumers.get(tag);
        return consumer != null && consumer.push(object);
    }

    //根据Tag关闭连接
    public static void stop(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            if (!consumers.isEmpty()) {
                if (!consumers.containsKey(tag)) {
                    logger.info("consumers no tag:" + tag);
                } else {
                    try {
                        QueueConsumer consumer = consumers.get(tag);
                        consumer.stopConsumer();
                        consumers.remove(tag);
                    } catch (Exception var2) {
                        var2.printStackTrace();
                    }

                }
            } else {
                logger.info("has none consumers!");
            }
        } else {
            logger.info("tag must be empty or null!");
        }
    }

    //根据地址和端口初始化连接
    public static void setup(String host, int port) {
        setup(null, host, port);
    }

    //根据参数启动初始化连接
    public static boolean setup(String tag, String host, int port) {
        ConnectConfig config = new ConnectConfig();
        config.setTag(tag);
        config.setHost(host);
        config.setPort(port);
        return setup(config);
    }

    //启动根据配置初始化连接服务端, 支持循环启动多个连接
    public static boolean setup(ConnectConfig config) {
        if (config == null) return false;
        boolean result = false;
        if (config.getTag() != null && !config.getTag().trim().isEmpty()) {
            config.setTag(config.getTag().toUpperCase());
        } else {
            config.setTag(DEFAULT_CONSUMER_TAG);
        }

        //避免相同Tag重复启动
        if (consumers.containsKey(config.getTag())) {
            logger.error("skip the same tag configuration!");
        } else if (config.getHost() != null && !config.getHost().trim().isEmpty() && config.getPort() > 0 && config.getPort() <= 65535) {
            if (startTwice()) {
                logger.error("run on singleTask mode ,another instance has already started!");
            } else {
                QueueConsumer consumer = new QueueConsumer(config);
                consumer.setDaemon(true);
                consumer.setName("Message consumer thread -> " + consumer.getConfig().getTag());
                consumer.start();
                consumers.put(config.getTag(), consumer);
                result = true;
            }
        } else {
            logger.error("host or port is not valid！");
        }

        return result;
    }

    //避免重复启动, 加文件锁.
    public static boolean startTwice() {
        boolean flag = false;

        try {
            File file = new File("lock");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileChannel channel = (new RandomAccessFile(file, "rws")).getChannel();
            FileLock lock = channel.tryLock();
            if (lock == null) {
                flag = true;
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return flag;
    }

    //日志写入到磁盘 log/日期.txt
    public static void write2Log(String text) {
        write2Log(null, text);
    }

    //指定文件名称写入到磁盘日志
    public static void write2Log(String fileTag, String text) {
        BufferedWriter bw = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTimeFormat = sdf.format(new Date());
            String fileName = dateTimeFormat.split(" ")[0];
            if (fileTag != null && !fileName.trim().isEmpty()) {
                fileName = fileTag.trim() + "." + fileName;
            }

            fileName = "log" + File.separator + fileName + ".txt";

            File file = new File(fileName);
            boolean exists = file.getParentFile().exists();
            if (!exists) {
                file.getParentFile().mkdirs();
            }
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
            bw.write(String.format("%s %s", dateTimeFormat, text));
            bw.newLine();
            bw.flush();
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

    }

}
