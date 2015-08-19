package com.alibaba.middleware.race.mom.broker;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by wlw on 15-8-6.
 */
public class GroupChannelManager {

    private GroupChannelManager(){}

    private static GroupChannelManager instance=new GroupChannelManager();

    public static GroupChannelManager getInstance() {
        return instance;
    }

    private ConcurrentMap<String,GroupChannel> channelMap= Maps.newConcurrentMap();

    public void addChannel(String groupId,Channel channel)
    {
        GroupChannel gc=channelMap.get(groupId);
        if(gc==null)
        {
            GroupChannel groupChannel=new DefaultGroupChannel(groupId);
            gc=channelMap.putIfAbsent(groupId,groupChannel);
            if(gc==null)
            {
                gc=groupChannel;
            }
        }
        gc.addChannel(channel);
    }

    public GroupChannel addGroupChannel(String groupId,GroupChannel groupChannel)
    {
        return channelMap.putIfAbsent(groupId,groupChannel);
    }

    public GroupChannel getChannelGroup(String groupId)
    {
        return channelMap.get(groupId);
    }
}
