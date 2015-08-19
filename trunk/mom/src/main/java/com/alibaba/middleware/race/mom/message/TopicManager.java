package com.alibaba.middleware.race.mom.message;

import com.alibaba.middleware.race.mom.Message;

import java.util.List;

/**
 * Created by wlw on 15-8-5.
 */
public interface TopicManager<T> {
    void addTopic(String topic,String filter,T t);
    List<T> getTopic(String topic,String filter);
    List<T> filterByMessage(Message message);
}
