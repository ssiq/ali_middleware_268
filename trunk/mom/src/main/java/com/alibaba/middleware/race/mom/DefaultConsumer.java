package com.alibaba.middleware.race.mom;

import com.alibaba.middleware.race.mom.consumer.ReceiveRequestService;
import com.alibaba.middleware.race.mom.consumer.SubscriptMessage;
import com.alibaba.middleware.race.mom.error.InvokeFailException;
import com.alibaba.middleware.race.mom.error.RemoteRunOuttimeException;
import com.alibaba.middleware.race.mom.message.DefaultTopicManager;
import com.alibaba.middleware.race.mom.message.TopicManager;
import com.alibaba.middleware.race.mom.model.MessageConst;
import com.alibaba.middleware.race.mom.model.TransmittingMessage;
import com.alibaba.middleware.race.mom.serializer.Serializer;
import com.alibaba.middleware.race.mom.service.ActiveService;
import com.alibaba.middleware.race.mom.service.Client;
import io.netty.channel.Channel;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wlw on 15-8-2.
 */
public class DefaultConsumer implements Consumer {

    private String groupId;
    private Client client;
    private TopicManager<MessageListener> topicManager=new DefaultTopicManager<>();
    private SubscriptMessage subscriptMessage=new SubscriptMessage();
    private Serializer serializer=Serializer.getSubscriptSerialier();

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String sip;

    @Override
    public void start() {
        sip=System.getProperty("SIP");
        long timeout=30000;
        int asyncpermit=300;
        client=new Client(asyncpermit,timeout);
        client.addResponseService(MessageConst.getBROAD(),
                new ReceiveRequestService(topicManager),
                Executors.newFixedThreadPool(30));
        int port=9999;
        final TransmittingMessage transmittingMessage=TransmittingMessage.wrapRequestMessage(
                MessageConst.getSBSCRIPT(), serializer.encode(this.subscriptMessage));
        while (true)
        {
            try {
                client.connect(sip, port, new ActiveService() {
                    @Override
                    public void process(Channel channel) {
                        while (true){
                            try {
                                client.doSyncInvoke(sip, transmittingMessage);
                                break;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (InvokeFailException e) {
                                e.printStackTrace();
                            } catch (RemoteRunOuttimeException e) {
                                e.printStackTrace();
                            }
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.sleep(3000);
            }
        }
    }

    @Override
    public void subscribe(String topic, String filter, MessageListener listener) {
        this.subscriptMessage.addSubscript(topic,filter);
        this.topicManager.addTopic(topic,filter,listener);
    }

    @Override
    public void setGroupId(String groupId) {
        this.groupId=groupId;
        this.subscriptMessage.setGroupId(groupId);
    }

    @Override
    public void stop() {
        client.doOneWayInvoke(sip, TransmittingMessage.wrapRequestMessage(MessageConst.getCLOSE(),null));
        client.shutdown();
    }
}
