package com.alibaba.middleware.race.mom.broker;

import com.alibaba.middleware.race.mom.consumer.SubscriptMessage;
import com.alibaba.middleware.race.mom.message.TopicManager;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.alibaba.middleware.race.mom.serializer.Serializer;
import com.alibaba.middleware.race.mom.service.AbstractRequestService;
import com.alibaba.middleware.race.mom.service.Pair;
import com.alibaba.middleware.race.mom.service.RequestProcesser;
import io.netty.channel.ChannelHandlerContext;

import java.util.Iterator;

/**
 * Created by wlw on 15-8-5.
 */
public class SubscriptRequestService extends AbstractRequestService {

    private TopicManager<GroupChannel> channelTopicManager;
    private Serializer subscriptSerializer=Serializer.getSubscriptSerialier();
    private GroupChannelManager groupChannelManager;

    public SubscriptRequestService(TopicManager<GroupChannel> channelTopicManager, GroupChannelManager groupChannelManager) {
        this.channelTopicManager = channelTopicManager;
        this.groupChannelManager = groupChannelManager;
    }

    @Override
    public void process(ChannelHandlerContext ctx, TransmittingMessage transmittingMessage) {
        SubscriptMessage subscriptMessage=new SubscriptMessage();
        subscriptSerializer.decode(transmittingMessage.getBody(), subscriptMessage);
        System.out.println("receive subscript message:" + subscriptMessage.toString());
        String groupId=subscriptMessage.getGroupId();
        GroupChannel gc=groupChannelManager.getChannelGroup(groupId);
        groupChannelManager.addChannel(groupId,ctx.channel());
        GroupChannel groupChannel=groupChannelManager.getChannelGroup(groupId);
        if(gc==null){
            Iterator<Pair<String,String>> iterator=subscriptMessage.getIterator();
            while (iterator.hasNext())
            {
                Pair<String,String> pair=iterator.next();
                channelTopicManager.addTopic(pair.getO1(),pair.getO2(),groupChannel);
            }
        }
        System.out.println("end subscript");
        doResponse(ctx, transmittingMessage, TransmittingMessage.wrapResponseMessage(-1, null));
    }
}
