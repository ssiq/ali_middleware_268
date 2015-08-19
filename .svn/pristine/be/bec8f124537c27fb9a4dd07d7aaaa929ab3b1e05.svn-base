package com.alibaba.middleware.race.mom.store;

import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.broker.GroupChannel;
import com.alibaba.middleware.race.mom.message.TopicManager;
import com.alibaba.middleware.race.mom.model.IndexMessage;
import com.alibaba.middleware.race.mom.model.NotSendMessagePair;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wlw on 15-8-6.
 */
public interface MessageStore {
    boolean start();
    ConcurrentLinkedQueue<NotSendMessagePair> getDonotSendMessage();//it will return a map from groupId to message List
    IndexMessage storeMessage(Message message, byte[] messageByte);//存储一条信息
    void registerSubscript(String groupId, String topic, String filter, long subscriptTime);//记录订阅信息
    TopicManager<GroupChannel> getSubsciptInfo();//返回订阅的信息
//    void setSendOver(String messageId);//设置某条消息消费完成
    void shutdown();//将所有的使用的东西force到硬盘,关闭force进程
//    void setRegistered(String messageId);
    void force();
}
