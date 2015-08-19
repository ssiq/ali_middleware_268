package com.alibaba.middleware.race.mom.message;

import com.alibaba.middleware.race.mom.service.Pair;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

/**
 * Created by wlw on 15-8-5.
 */
public class DefaultTopicManager<T> extends AbstractTopicManager<T> {

    public DefaultTopicManager() {
        topicMap= Maps.newConcurrentMap();
        topicNoFilterMap= LinkedListMultimap.create();
        topicNoFilterMap= Multimaps.synchronizedListMultimap(topicNoFilterMap);
    }

    @Override
    protected ListMultimap<Pair<String,String>, T> getTopicValue(String topic, boolean isAdd){
        ListMultimap<Pair<String,String>,T> res;
        if(isAdd)
        {
            synchronized (topicMap)
            {
                res=topicMap.get(topic);
                if(res==null)
                {
                    res=LinkedListMultimap.create();
                    res=Multimaps.synchronizedListMultimap(res);
                    topicMap.put(topic, res);
                }
            }
        }else{
            res=topicMap.get(topic);
        }
        return res;
    }

}
