# Kafka转发工具
Java Socker 转发Kafka数据工具

# 介绍
处理如下环境中, 两个Kafka集群实时同步数据.
* 有两个Kafka集群. 
* 两个集群都是各自的内网集群没有外网端口. 
* 集群都有堡垒机可以对外开放端口.




# 目录简介
```text
com..
  ├─consumer   //Kafka消费者相关.
  |
  ├─receive    //Socker服务端+Kafka生产者.
  |
  ├─sender     //Socker客户端发送数据到服务端.
  |
  └─test       //测试
```



