package com.alibaba.middleware.race.mom.message;

import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.service.Pair;
import com.google.common.collect.*;

import java.util.*;

/**
 * Created by wlw on 15-8-5.
 */
public abstract class AbstractTopicManager<T> implements TopicManager<T>{
    protected Map<String,ListMultimap<Pair<String,String>,T>> topicMap;
    protected ListMultimap<String,T> topicNoFilterMap;

    protected abstract ListMultimap<Pair<String,String>,T> getTopicValue(String topic,boolean isAdd);

    private Pair<String,String> decodeFilter(String filter)
    {
//        if(filter==null)
//        {
//            return null;
//        }
        String[] strings=filter.split("=");
        return new Pair<>(strings[0],strings[1]);
    }

    public void addTopic(String topic,String filter,T t)
    {
        if(filter!=null&&filter.contains("="))
        {
            ListMultimap<Pair<String,String>,T> map=getTopicValue(topic, true);
            Pair<String,String> pair=decodeFilter(filter);
            map.put(pair,t);
        }else{
//            System.out.println("add one to null filter");
            topicNoFilterMap.put(topic,t);
        }
    }

    public List<T> getTopic(String topic,String filter)
    {
        if(filter!=null)
        {
            ListMultimap<Pair<String,String>,T> listMultimap=topicMap.get(topic);
            if(listMultimap==null)
            {
                return null;
            }else{
                return listMultimap.get(decodeFilter(filter));
            }
        }else{
            return topicNoFilterMap.get(topic);
        }
    }

    public List<T> filterByMessage(Message message)
    {
        String topic=message.getTopic();
        ListMultimap<Pair<String,String>,T> listMultimap=topicMap.get(topic);
        List<T> list=new LinkedList<>();
        if(listMultimap!=null)
        {
            Set<Pair<String,String>> set= listMultimap.keySet();
            Iterator<Pair<String,String>> iterator=set.iterator();
            while (iterator.hasNext())
            {
                Pair<String,String> pair=iterator.next();
                String value=message.getProperty(pair.getO1());
                if(value!=null&&value.equals(pair.getO2()))
                {
                    list.addAll(listMultimap.get(pair));
                }
            }
        }
        List<T> tempList=topicNoFilterMap.get(topic);
        if(tempList!=null)
        {
            list.addAll(tempList);
        }
//        System.out.println("temp list size is"+tempList.size());
//        System.out.println("the list size is"+list.size());
        return list;
    }

    @Override
    public String toString() {
        return "AbstractTopicManager{" +
                "topicMap=" + topicMap +
                ", topicNoFilterMap=" + topicNoFilterMap +
                '}';
    }
}
