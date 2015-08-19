package com.alibaba.middleware.race.mom.consumer;

import com.alibaba.middleware.race.mom.ConsumeResult;
import com.alibaba.middleware.race.mom.ConsumeStatus;
import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.MessageListener;
import com.alibaba.middleware.race.mom.message.TopicManager;
import com.alibaba.middleware.race.mom.model.MessageConst;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.alibaba.middleware.race.mom.serializer.Serializer;
import com.alibaba.middleware.race.mom.service.AbstractRequestService;
import com.alibaba.middleware.race.mom.service.RequestProcesser;
import io.netty.channel.ChannelHandlerContext;

import java.util.Iterator;
import java.util.List;

/**
 * Created by wlw on 15-8-5.
 */
public class ReceiveRequestService extends AbstractRequestService {

    private TopicManager<MessageListener> topicManager;
    private Serializer messageSerializer=Serializer.getMessageSerialier();
    private Serializer consumerResult=Serializer.getConsumnerResultSerialier();

    public ReceiveRequestService(TopicManager<MessageListener> topicManager) {
        this.topicManager = topicManager;
    }

    @Override
    public void process(ChannelHandlerContext ctx, TransmittingMessage transmittingMessage) {
//        System.out.println("receive one message");
        Message message=new Message();
        messageSerializer.decode(transmittingMessage.getBody(),message);
        List<MessageListener> messageListeners=topicManager.filterByMessage(message);
        Iterator<MessageListener> iterator=messageListeners.iterator();
        ConsumeResult consumeResult=new ConsumeResult();
        consumeResult.setInfo("have no subscript the message");
        consumeResult.setStatus(ConsumeStatus.FAIL);
//        System.out.println("begin do listener");
        while(iterator.hasNext())
        {
//            System.out.println("do the message listener");
            MessageListener messageListener=iterator.next();
            consumeResult=messageListener.onMessage(message);
            break;
        }
//        System.out.println("end do listener");
        TransmittingMessage response=TransmittingMessage.wrapResponseMessage(
                MessageConst.getBROADRESULT(),consumerResult.encode(consumeResult));
//        System.out.println("end receive message");
        doResponse(ctx,transmittingMessage,response);
    }
}
