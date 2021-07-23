package com.twdt.receive;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueGCTask extends TimerTask {
    private final Logger logger = LoggerFactory.getLogger(QueueGCTask.class);
    private final QueueConsumer consumer;

    public QueueGCTask(QueueConsumer consumer) {
        this.consumer = consumer;
    }

    public void run() {
        long removeCount = 0;

        LinkedBlockingQueue<String> queue;

        for (queue = consumer.getQueue(); queue.size() >= consumer.getConfig().getQueueSize() * consumer.getConfig().getGcThreshold(); ++removeCount) {
            //删除队列的头部.
            queue.remove();
        }

        this.consumer.getConfig().setRemoveCount(this.consumer.getConfig().getRemoveCount() + removeCount);
        this.logger.debug("queue_name:{} max queue size:{},gc size:{},current queue size:{} pushCount:{} timeOutCount:{}", new Object[]{this.consumer.getConfig().getTag(), this.consumer.getConfig().getQueueSize(), removeCount, queue.size(), this.consumer.getConfig().getPushCount(), this.consumer.getConfig().getTimeOutCount()});
    }
}
