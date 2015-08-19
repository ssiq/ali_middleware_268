package com.alibaba.middleware.race.mom.broker;

import com.alibaba.middleware.race.mom.error.AsyncInvokeTooQuickException;
import com.alibaba.middleware.race.mom.model.*;
import com.alibaba.middleware.race.mom.service.Pair;
import com.alibaba.middleware.race.mom.service.Server;
import com.alibaba.middleware.race.mom.store.MessageStore;
import com.alibaba.middleware.race.mom.store.impl.QueueListMessageStore;
import com.alibaba.middleware.race.mom.util.SimpleQueue;
import com.alibaba.middleware.race.mom.util.WrapConcurrentListQueue;
import io.netty.channel.Channel;

import java.net.BindException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wlw on 15-8-2.
 */
public class Broker {

    private Server server;
//    private TopicManager<GroupChannel> channelTopicManager=new DefaultTopicManager<>();
    private GroupChannelManager groupChannelManager=GroupChannelManager.getInstance();
    private InvokeInterface invokeInterface;
    private String brokerName="";
    private MessageId messageId=new MessageId();
    private MessageStore messageStore;
    private ConcurrentLinkedQueue<IndexMessage> unsendMessage=new ConcurrentLinkedQueue<>();
    private int fileSize=4096;
    private PostMessageRequstService postMessageRequstService;
    private SimpleQueue<Pair<GroupChannel,IndexMessage>> unsendQueue=
            new WrapConcurrentListQueue<Pair<GroupChannel,IndexMessage>>(
            new ConcurrentLinkedQueue<Pair<GroupChannel,IndexMessage>>());

    public Broker(String storePath) {
        int asyncpermit=300;
        int timeout=10000;
        server=new Server(asyncpermit,timeout);
        invokeInterface=server.getInvokeProxy();
    }

    public Broker(String storePath,String brokerName) {
        this(storePath);
        this.brokerName = brokerName;
        messageId.setBrokername(brokerName);
    }

    public Broker(String storePath,String brokerName,int fileSize)
    {
        this(storePath,brokerName);
        this.fileSize=fileSize;
        postMessageRequstService=new PostMessageRequstService(
                invokeInterface,messageId, ContextExecutorService.getExecutorService(),unsendQueue);
        messageStore=new QueueListMessageStore(
                fileSize,storePath,ContextExecutorService.getExecutorService(),
                postMessageRequstService.getRunableFactory());
        postMessageRequstService.setMessageStore(messageStore);
    }
//    private int coreSize=2;
//    private ExecutorService schedExecutorService=Executors.newScheduledThreadPool(coreSize);
    private ScheduledExecutorService resendUnsentMessage=Executors.newSingleThreadScheduledExecutor();

    public void start(int port) throws BindException
    {
        if(!messageStore.start())System.exit(1);
        //this map key is the groupId,value is the not send message id
//        final ConcurrentLinkedQueue<Pair<GroupChannel,IndexMessage>> unsendQueue=new ConcurrentLinkedQueue<>();
        final ExecutorService executorService=ContextExecutorService.getExecutorService();
//        PostMessageRequstService postMessageRequstService=new PostMessageRequstService(invokeInterface,messageId,messageStore,executorService,unsendQueue);
        SubscriptRequestService subscriptRequestService=new SubscriptRequestService(
                messageStore.getSubsciptInfo(), groupChannelManager);
        postMessageRequstService.setChannelTopicManager(messageStore.getSubsciptInfo());

        server.addResponseService(MessageConst.getPOST(),postMessageRequstService, executorService);
        server.addResponseService(MessageConst.getSBSCRIPT(),subscriptRequestService,executorService);
        server.addResponseService(MessageConst.getCLOSE(),new CloseRequestService(),executorService);
        server.addResponseService(MessageConst.getHEARTBEAT(),new HeartbeatService(),executorService);
        server.bind(port);
        resendUnsentMessage.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int size=unsendQueue.size();
                while (size>0)
                {
                    final Pair<GroupChannel,IndexMessage> pair=unsendQueue.get();
                    if(pair==null)break;
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            GroupChannel g = pair.getO1();
                            final IndexMessage indexMessage = pair.getO2();
                            Channel channel = g.getNowChannel();
                            final AtomicBoolean isPut = new AtomicBoolean(false);
                            if (channel == null) {
                                try {
                                    invokeInterface.invokeAsync(channel,
                                            TransmittingMessage.wrapRequestMessage(MessageConst.getBROAD(), indexMessage.getMessage()),
                                            new CallbackListener() {
                                                @Override
                                                public void invoke(ResponseFuture responseFuture) {
                                                    if(responseFuture.isOK())
                                                    {
                                                        System.out.println("resent one success");
                                                        indexMessage.down();
                                                    } else {
                                                        unsendQueue.putOnce(pair, isPut);
                                                    }
                                                }
                                            });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (AsyncInvokeTooQuickException e) {
                                    e.printStackTrace();
                                    unsendQueue.putOnce(pair, isPut);
                                }
                            }
                        }
                    });
                    --size;
                }
            }
        },0,2,TimeUnit.SECONDS);
    }

    public void shutdown()
    {
        server.shutdown();
        messageStore.shutdown();
    }
}
