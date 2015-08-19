package com.alibaba.middleware.race.mom.consumer;

import com.alibaba.middleware.race.mom.service.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wlw on 15-8-5.
 */
public class SubscriptMessage {
    private List<Pair<String,String>> subscriptList=new LinkedList<>();
    private String groupId;

    public SubscriptMessage(String groupId) {
        this.groupId = groupId;
    }

    public SubscriptMessage() {
    }

    public void addSubscript(String topic,String filter)
    {
        this.subscriptList.add(new Pair<String, String>(topic,filter));
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Iterator<Pair<String,String>> getIterator()
    {
        return subscriptList.iterator();
    }

    @Override
    public String toString() {
        return "SubscriptMessage{" +
                "subscriptList=" + subscriptList +
                ", groupId='" + groupId + '\'' +
                '}';
    }
}
