package race.momtest.messageStoreTest;

import com.alibaba.middleware.race.mom.Message;
import com.alibaba.middleware.race.mom.broker.GroupChannel;
import com.alibaba.middleware.race.mom.message.DefaultTopicManager;
import com.alibaba.middleware.race.mom.message.TopicManager;
import com.alibaba.middleware.race.mom.store.queue.SubscriptQueue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wlw on 15-8-13.
 */
public class TestSubscriptQueue {

    private static void getAndShow(TopicManager<GroupChannel> topicManager,String topic,String filter)
    {
        Message message=new Message();
        message.setTopic(topic);
        String[]strings=filter.split("=");
        message.setProperty(strings[0],strings[1]);
        List<GroupChannel> list=topicManager.filterByMessage(message);
        Iterator<GroupChannel> iterator=list.iterator();
        System.out.println("topic:"+topic+" filter:"+filter);
        while (iterator.hasNext())
        {
            GroupChannel groupChannel=iterator.next();
            System.out.println("group id:"+groupChannel.getGroupId());
        }
    }

    public static void main(String[]args) throws IOException {
        String name=TestFilePath.path+"subscipt";
        int fileLength=4096*4;
        TopicManager<GroupChannel> topicManager=new DefaultTopicManager<>();
        SubscriptQueue subscriptQueue=new SubscriptQueue(name,fileLength,topicManager);
        subscriptQueue.start();
        String groupId1="1";
        String topic1="a";
        String filter1="3=6";
        String groupId2="2";
        String topic2="b";
        String filter2="3=6";
        long time=System.currentTimeMillis();
        subscriptQueue.addSubscipt(groupId1, topic1, filter1, time);
        subscriptQueue.addSubscipt(groupId2, topic2, filter2, time);
        subscriptQueue.force();
        subscriptQueue.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        topicManager=new DefaultTopicManager<>();
        subscriptQueue=new SubscriptQueue(name,fileLength,topicManager);
        subscriptQueue.start();
        getAndShow(topicManager,topic1,filter1);
        getAndShow(topicManager,topic2,filter2);
//        System.out.println(topicManager.toString());
        subscriptQueue.remove();
    }
}
