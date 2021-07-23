package com.twdt.receive;


/**
 * 连接配置, 对数据做一些统计
 */
public class ConnectConfig {
    private String tag;
    private String host;
    private int port;
    private int queueSize = ServerConnectorUtil.DEFAULT_MAX_QUEUE_SIZE;
    private float gcThreshold;
    private long timeOutThreshold;
    private long pushCount;
    private long sendCount;
    private long timeOutCount;
    private long removeCount;
    private boolean singleTask;
    private boolean needSendStatus;

    public ConnectConfig() {
        this.gcThreshold = ServerConnectorUtil.DEFAULT_QUEUE_GC_THRESHOLD;
        this.timeOutThreshold = -1L;
        this.pushCount = 0L;
        this.sendCount = 0L;
        this.timeOutCount = 0L;
        this.removeCount = 0L;
        this.singleTask = true;
        this.needSendStatus = true;
    }

    public long getRemoveCount() {
        return this.removeCount;
    }

    public void setRemoveCount(long removeCount) {
        this.removeCount = removeCount;
    }

    public boolean isNeedSendStatus() {
        return this.needSendStatus;
    }

    public void setNeedSendStatus(boolean needSendStatus) {
        this.needSendStatus = needSendStatus;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public float getGcThreshold() {
        return this.gcThreshold;
    }

    public void setGcThreshold(float gcThreshold) {
        this.gcThreshold = gcThreshold;
    }

    public long getPushCount() {
        return this.pushCount;
    }

    public void setPushCount(long pushCount) {
        this.pushCount = pushCount;
    }

    public long getSendCount() {
        return this.sendCount;
    }

    public void setSendCount(long sendCount) {
        this.sendCount = sendCount;
    }

    public long getTimeOutCount() {
        return this.timeOutCount;
    }

    public void setTimeOutCount(long timeOutCount) {
        this.timeOutCount = timeOutCount;
    }

    public long getTimeOutThreshold() {
        return this.timeOutThreshold;
    }

    public void setTimeOutThreshold(long timeOutThreshold) {
        this.timeOutThreshold = timeOutThreshold;
    }

    public boolean isSingleTask() {
        return this.singleTask;
    }

    public void setSingleTask(boolean singleTask) {
        this.singleTask = singleTask;
    }
}
