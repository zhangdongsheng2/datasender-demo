package com.twdt.sender;


import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class CountTask extends TimerTask {
    private QueueConsumer consumer = null;
    private long preSendCount = 0L;
    private long prePushCount = 0L;
    private long preTimeOutCount = 0L;
    private long preRemoveCount = 0L;

    public CountTask(QueueConsumer consumer) {
        this.consumer = consumer;
    }

    public void run() {
        long subSendCount = this.consumer.getConfig().getSendCount() - this.preSendCount;
        this.preSendCount = this.consumer.getConfig().getSendCount();
        long subPushCount = this.consumer.getConfig().getPushCount() - this.prePushCount;
        this.prePushCount = this.consumer.getConfig().getPushCount();
        long subTimeOuntCount = this.consumer.getConfig().getTimeOutCount() - this.preTimeOutCount;
        this.preTimeOutCount = this.consumer.getConfig().getTimeOutCount();
        long subRemoveCount = this.consumer.getConfig().getRemoveCount() - this.preRemoveCount;
        this.preRemoveCount = this.consumer.getConfig().getRemoveCount();
        if (this.consumer.getConfig().isNeedSendStatus()) {
            JSONObject json = new JSONObject();
            json.put("queue_name", this.consumer.getConfig().getTag());
            json.put("max_queue", this.consumer.getConfig().getQueueSize());
            json.put("gc_size", subRemoveCount);
            json.put("cur_size", this.consumer.getQueue().size());
            json.put("push_count", subPushCount);
            json.put("send_count", subSendCount);
            json.put("timeout_count", subTimeOuntCount);
            json.put("time", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
            this.consumer.push("interfaces:" + json.toJSONString());
        }

        this.consumer.getConfig().setRemoveCount(0L);
        ServerConnectorUtil.write2Log("Send_count", String.format("tag:%s\tpush_count:%s\tsend_count:%s\ttimeout_count:%s\tremove_count:%s", this.consumer.getConfig().getTag(), subPushCount, subSendCount, subTimeOuntCount, subRemoveCount));
    }
}
